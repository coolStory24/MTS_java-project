package org.example.user;

import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserServiceImplementationTest {

  @Container
  public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:13");
  private static final Logger LOG = Logger.getLogger(UserServiceImplementationTest.class.getName());
  private static Jdbi jdbi;

  @BeforeAll
  static void beforeAll(){
    String postgresJdbcUrl = POSTGRES.getJdbcUrl();
    Flyway flyway =
            Flyway.configure()
                    .outOfOrder(true)
                    .locations("classpath:db/migrations")
                    .dataSource(postgresJdbcUrl, POSTGRES.getUsername(), POSTGRES.getPassword())
                    .load();
    flyway.migrate();
    LOG.info("PostgreSQL has been successfully initialized: " + postgresJdbcUrl + ", " +POSTGRES.getUsername());
    jdbi = Jdbi.create(postgresJdbcUrl, POSTGRES.getUsername(), POSTGRES.getPassword());
  }

  @BeforeEach

  void beforeEach() {
    jdbi.useTransaction(handle -> handle.createUpdate("DELETE FROM user").execute());
  }

  @Test
  @DisplayName("UserServiceImplementationTest -- tests findUserById functionality")
  void findUserById() {
    UserRepository userRepository = new UserRepositoryImplementation(jdbi);
    UserServiceImplementation userService = new UserServiceImplementation(userRepository);

    long userId = userService.createUser("User to find");

    UserRepository.UserEntity foundUser = userService.findUserById(userId);

    assertEquals("User to find", foundUser.name());
    assertEquals(userId, foundUser.id());

  }

  @Test
  @DisplayName("UserServiceImplementationTest -- tests createUser functionality")
  void shouldCreateUser() {
    UserRepository userRepository = new UserRepositoryImplementation(jdbi);
    UserServiceImplementation userService = new UserServiceImplementation(userRepository);

    long userId = userService.createUser("John Smith");
    UserRepository.UserEntity user = userService.findUserById(userId);

    assertEquals("John Smith", user.name());
    assertEquals(userId, user.id());

  }

  @Test
  @DisplayName("UserServiceImplementationTest -- tests updateUser functionality")
  void shouldUpdateUser() {
    UserRepository userRepository = new UserRepositoryImplementation(jdbi);
    UserServiceImplementation userService = new UserServiceImplementation(userRepository);

    long userId = userService.createUser("User to update");
    assertNotNull(userService.findUserById(userId));

    userService.updateUser(userId,"Updated user");
    UserRepository.UserEntity updatedUser = userService.findUserById(userId);

    assertEquals("Updated user", updatedUser.name());
    assertEquals(userId, updatedUser.id());

  }

  @Test
  @DisplayName("UserServiceImplementationTest -- tests deleteUser functionality")
  void shouldDeleteUser() {
    UserRepository userRepository = new UserRepositoryImplementation(jdbi);
    UserServiceImplementation userService = new UserServiceImplementation(userRepository);

    long userId = userService.createUser("User to delete");

    assertNotNull(userService.findUserById(userId));

    userService.deleteUser(userId);

    assertThrows(UserExceptions.UserNotFoundException.class, () -> userService.findUserById(userId));
  }
}