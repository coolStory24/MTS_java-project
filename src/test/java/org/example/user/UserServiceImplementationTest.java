package org.example.user;

import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserServiceImplementationTest {

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
    jdbi.useTransaction(handle -> handle.createUpdate("DELETE FROM room").execute());
  }

  @Test
  void findUserById() {
  }

  @Test
  void createUser() {
  }

  @Test
  void updateUser() {
  }

  @Test
  void deleteUser() {
  }
}