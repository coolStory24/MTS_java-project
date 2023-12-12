package org.example.user;

import static org.junit.jupiter.api.Assertions.*;

import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DisplayName("Pg user repository test")
class UserRepositoryImplementationTest {
  @Container
  public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:13");

  private static Jdbi jdbi;

  @BeforeAll
  static void beforeAll() {
    String postgresJdbcUrl = POSTGRES.getJdbcUrl();
    Flyway flyway =
        Flyway.configure()
            .outOfOrder(true)
            .locations("classpath:db/migrations")
            .dataSource(postgresJdbcUrl, POSTGRES.getUsername(), POSTGRES.getPassword())
            .load();
    flyway.migrate();
    jdbi = Jdbi.create(postgresJdbcUrl, POSTGRES.getUsername(), POSTGRES.getPassword());
  }

  @BeforeEach
  void beforeEach() {
    jdbi.useTransaction(handle -> handle.createUpdate("DELETE FROM \"user\"").execute());
  }

  @Test
  @DisplayName("Should create new user")
  void shouldCreateNewUser() {
    UserRepository repository = new UserRepositoryImplementation(jdbi);

    long userId = repository.create("John Doe");

    UserRepository.UserEntity user = repository.getById(userId);
    assertEquals("John Doe", user.name());
    assertEquals(userId, user.id());
  }

  @Test
  @DisplayName("Should delete user by id")
  void shouldDeleteUserById() {
    UserRepository repository = new UserRepositoryImplementation(jdbi);

    long userId = repository.create("UserToDelete");

    assertNotNull(repository.getById(userId));

    repository.delete(userId);

    assertThrows(UserExceptions.UserDatabaseException.class, () -> repository.getById(userId));
  }

  @Test
  @DisplayName("Should update user by id")
  void shouldUpdateUserById() {
    UserRepository repository = new UserRepositoryImplementation(jdbi);

    long userId = repository.create("UserToUpdate");

    assertNotNull(repository.getById(userId));

    repository.update(userId, "UpdatedUser");

    UserRepository.UserEntity updatedUser = repository.getById(userId);

    assertEquals("UpdatedUser", updatedUser.name());
    assertEquals(userId, updatedUser.id());
  }

  @Test
  @DisplayName("Should find user by id")
  void shouldFindUserById() {
    UserRepository repository = new UserRepositoryImplementation(jdbi);

    long userId = repository.create("UserToFind");

    assertNotNull(repository.getById(userId));

    UserRepository.UserEntity foundUser = repository.getById(userId);

    assertEquals("UserToFind", foundUser.name());
    assertEquals(userId, foundUser.id());
  }

  @Test
  @DisplayName("Should throw UserDatabaseException when creating user with invalid parameters")
  void shouldThrowExceptionWhenCreatingUserWithInvalidParameters() {
    UserRepository repository = new UserRepositoryImplementation(jdbi);

    UserExceptions.UserDatabaseException exception =
        assertThrows(UserExceptions.UserDatabaseException.class, () -> repository.create(null));

    assertEquals("Cannot create user", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw UserDatabaseException when updating user with invalid parameters")
  void shouldThrowExceptionWhenUpdatingUserWithInvalidParameters() {
    UserRepository repository = new UserRepositoryImplementation(jdbi);

    long userId = repository.create("UserToUpdate");

    UserExceptions.UserDatabaseException exception =
        assertThrows(
            UserExceptions.UserDatabaseException.class, () -> repository.update(userId, null));

    assertEquals("Cannot update user", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw UserDatabaseException when deleting user with invalid id")
  void shouldThrowExceptionWhenDeletingUserWithInvalidId() {
    UserRepository repository = new UserRepositoryImplementation(jdbi);

    long invalidUserId = -1;

    UserExceptions.UserDatabaseException exception =
        assertThrows(
            UserExceptions.UserDatabaseException.class, () -> repository.delete(invalidUserId));

    assertEquals("Cannot delete user", exception.getMessage());
  }
}
