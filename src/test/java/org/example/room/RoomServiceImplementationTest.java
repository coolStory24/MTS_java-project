package org.example.room;

import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalTime;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class RoomServiceImplementationTest {

  @Container
  public static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:13");

  private static final Logger LOG = Logger.getLogger(RoomServiceImplementationTest.class.getName());
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
    LOG.info(
        "PostgreSQL has been successfully initialized: "
            + postgresJdbcUrl
            + ", "
            + POSTGRES.getUsername());
    jdbi = Jdbi.create(postgresJdbcUrl, POSTGRES.getUsername(), POSTGRES.getPassword());
  }

  @BeforeEach
  void beforeEach() {
    jdbi.useTransaction(handle -> handle.createUpdate("DELETE FROM room").execute());
  }

  @Test
  @DisplayName("RoomServiceImplementationTest -- tests getRoomById functionality")
  void shouldGetRoomById() {
    RoomRepository roomRepository = new RoomRepositoryImplementation(jdbi);
    RoomServiceImplementation roomService = new RoomServiceImplementation(roomRepository);

    long roomId =
        roomService.createRoom("Room #1", LocalTime.parse("10:00:00"), LocalTime.parse("22:00:00"));

    RoomRepository.RoomEntity room = roomService.getRoomById(roomId);

    assertEquals("Room #1", room.title());
    assertEquals(LocalTime.parse("10:00:00"), room.startInterval());
    assertEquals(LocalTime.parse("22:00:00"), room.endInterval());
    assertEquals(roomId, room.id());
  }

  @Test
  @DisplayName("RoomServiceImplementationTest -- tests createRoom functionality")
  void shouldCreateRoom() {
    RoomRepository roomRepository = new RoomRepositoryImplementation(jdbi);
    RoomServiceImplementation roomService = new RoomServiceImplementation(roomRepository);

    long roomId =
        roomService.createRoom("Room #1", LocalTime.parse("10:00:00"), LocalTime.parse("22:00:00"));
    RoomRepository.RoomEntity room = roomService.getRoomById(roomId);
    assertEquals("Room #1", room.title());
    assertEquals(LocalTime.parse("10:00:00"), room.startInterval());
    assertEquals(LocalTime.parse("22:00:00"), room.endInterval());
    assertEquals(roomId, room.id());
  }

  @Test
  @DisplayName("RoomServiceImplementationTest -- tests updateRoom functionality")
  void shouldUpdateRoom() {
    RoomRepository roomRepository = new RoomRepositoryImplementation(jdbi);
    RoomServiceImplementation roomService = new RoomServiceImplementation(roomRepository);

    long roomId =
        roomService.createRoom("Room #1", LocalTime.parse("10:00:00"), LocalTime.parse("22:00:00"));
    roomService.updateRoom(
        roomId, "Room #1 (updated)", LocalTime.parse("07:00:00"), LocalTime.parse("19:00:00"));
    RoomRepository.RoomEntity room = roomService.getRoomById(roomId);

    assertEquals("Room #1 (updated)", room.title());
    assertEquals(LocalTime.parse("07:00:00"), room.startInterval());
    assertEquals(LocalTime.parse("19:00:00"), room.endInterval());
    assertEquals(roomId, room.id());
  }

  @Test
  @DisplayName("RoomServiceImplementationTest -- tests deleteRoom functionality")
  void shouldDeleteRoom() {
    RoomRepository roomRepository = new RoomRepositoryImplementation(jdbi);
    RoomServiceImplementation roomService = new RoomServiceImplementation(roomRepository);

    long roomId =
        roomService.createRoom("Room #1", LocalTime.parse("10:00:00"), LocalTime.parse("22:00:00"));
    assertNotNull(roomService.getRoomById(roomId));

    roomService.deleteRoom(roomId);

    var exception =
        assertThrows(
            RoomExceptions.RoomNotFoundException.class, () -> roomService.getRoomById(roomId));
  }
}
