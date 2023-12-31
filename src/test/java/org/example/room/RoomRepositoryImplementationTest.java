package org.example.room;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalTime;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DisplayName("Pg room repository test")
class RoomRepositoryImplementationTest {
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
    jdbi.useTransaction(handle -> handle.createUpdate("DELETE FROM room").execute());
  }

  @Test
  @DisplayName("Should create new room")
  void ShouldCreateNewRoom() {
    RoomRepository repository = new RoomRepositoryImplementation(jdbi);

    long orgId =
        repository.create("Room 1", LocalTime.parse("10:00:00"), LocalTime.parse("22:00:00"));

    RoomRepository.RoomEntity room = repository.getById(orgId);
    assertEquals("Room 1", room.title());
    assertEquals(LocalTime.parse("10:00:00"), room.startInterval());
    assertEquals(LocalTime.parse("22:00:00"), room.endInterval());
    assertEquals(orgId, room.id());
  }

  @Test
  @DisplayName("Should update room by id")
  void shouldUpdateRoomById() {
    RoomRepository repository = new RoomRepositoryImplementation(jdbi);

    long roomId =
        repository.create("RoomToUpdate", LocalTime.parse("08:00:00"), LocalTime.parse("17:00:00"));

    assertNotNull(repository.getById(roomId));

    repository.update(
        roomId, "UpdatedRoom", LocalTime.parse("07:00:00"), LocalTime.parse("16:00:00"));

    RoomRepository.RoomEntity updatedRoom = repository.getById(roomId);

    assertEquals("UpdatedRoom", updatedRoom.title());
    assertEquals(LocalTime.parse("07:00:00"), updatedRoom.startInterval());
    assertEquals(LocalTime.parse("16:00:00"), updatedRoom.endInterval());
    assertEquals(roomId, updatedRoom.id());
  }

  @Test
  @DisplayName("Should delete room by id")
  void shouldDeleteRoomById() {
    RoomRepository repository = new RoomRepositoryImplementation(jdbi);

    long roomId =
        repository.create("RoomToDelete", LocalTime.parse("09:00:00"), LocalTime.parse("18:00:00"));

    assertNotNull(repository.getById(roomId));

    repository.delete(roomId);

    var exception =
        assertThrows(RoomExceptions.RoomDatabaseException.class, () -> repository.getById(roomId));
    assertEquals("Cannot find room", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw RoomDatabaseException when creating room with invalid parameters")
  void shouldThrowExceptionWhenCreatingRoomWithInvalidParameters() {
    RoomRepository repository = new RoomRepositoryImplementation(jdbi);

    var exception =
        assertThrows(
            RoomExceptions.RoomDatabaseException.class,
            () ->
                repository.create(null, LocalTime.parse("08:00:00"), LocalTime.parse("17:00:00")));

    assertEquals("Cannot create room", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw RoomDatabaseException when updating room with invalid parameters")
  void shouldThrowExceptionWhenUpdatingRoomWithInvalidParameters() {
    RoomRepository repository = new RoomRepositoryImplementation(jdbi);

    long roomId =
        repository.create("RoomToUpdate", LocalTime.parse("08:00:00"), LocalTime.parse("17:00:00"));

    var exception =
        assertThrows(
            RoomExceptions.RoomDatabaseException.class,
            () ->
                repository.update(
                    roomId, null, LocalTime.parse("07:00:00"), LocalTime.parse("16:00:00")));

    assertEquals("Cannot update room", exception.getMessage());
  }

  @Test
  @DisplayName("Should throw RoomDatabaseException when deleting room with invalid id")
  void shouldThrowExceptionWhenDeletingRoomWithInvalidId() {
    RoomRepository repository = new RoomRepositoryImplementation(jdbi);

    long invalidRoomId = -1;

    var exception =
        assertThrows(
            RoomExceptions.RoomDatabaseException.class, () -> repository.delete(invalidRoomId));

    assertEquals("Cannot delete room", exception.getMessage());
  }
}
