package org.example.room;

import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class RoomServiceImplementationTest {

  @Container
  public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:13");
  private static final Logger LOG = Logger.getLogger(RoomServiceImplementationTest.class.getName());
  private static Jdbi jdbi;

  @BeforeAll
  static void beforeAll(){
    String postgresJdbcUrl = POSTGRES.getJdbcUrl();
    LOG.info("PostgreSQL has been successfully initialized: " + postgresJdbcUrl + ", " +POSTGRES.getUsername());
    jdbi = Jdbi.create(postgresJdbcUrl, POSTGRES.getUsername(), POSTGRES.getPassword());
  }

  @BeforeEach

  void beforeEach() {

  }

  @Test
  void getRoomById() {
  }

  @Test
  void createRoom() {
  }

  @Test
  void updateRoom() {
  }

  @Test
  void deleteRoom() {
  }
}