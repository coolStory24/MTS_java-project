package org.example.room;

import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import static org.junit.jupiter.api.Assertions.*;

class RoomServiceImplementationTest {

  @Container
  public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:13");

  private static Jdbi jdbi;

  @BeforeAll
  static void beforeAll(){
    String postgresJdbcUrl = POSTGRES.getJdbcUrl();
    System.out.print(postgresJdbcUrl + " " + POSTGRES.getUsername());
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