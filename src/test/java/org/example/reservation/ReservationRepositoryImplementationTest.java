package org.example.reservation;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.example.room.RoomRepository;
import org.example.room.RoomRepositoryImplementation;
import org.example.user.UserRepository;
import org.example.user.UserRepositoryImplementation;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Disabled("Reservation repository methods must be implemented first")
@Testcontainers
@DisplayName("Pg reservation repository test")
class ReservationRepositoryImplementationTest {
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

    UserRepository userRepository = new UserRepositoryImplementation(jdbi);
    RoomRepository roomRepository = new RoomRepositoryImplementation(jdbi);

    userRepository.create("John");
    roomRepository.create("Room1", "10:00:00", "20:00:00");
  }

  @BeforeEach
  void beforeEach() {
    jdbi.useTransaction(handle -> handle.createUpdate("DELETE FROM reservation").execute());
  }

  @Test
  @DisplayName("Should retrieve reservation by ID")
  void shouldRetrieveReservationById() throws ReservationExceptions.ReservationDatabaseException {
    ReservationRepository repository = new ReservationRepositoryImplementation(jdbi);

    long reservationId =
        repository.createReservation(
            LocalTime.of(12, 30, 0), LocalTime.of(13, 30, 0), LocalDate.of(2023, 12, 1), 1L, 1L);

    ReservationRepository.ReservationEntity reservation = repository.getById(reservationId);

    assertEquals(reservationId, reservation.id());
  }

  @Test
  @DisplayName("Should retrieve all reservations for a room on a specific date")
  void shouldRetrieveAllReservationsForRoomOnDate()
      throws ReservationExceptions.ReservationDatabaseException {
    ReservationRepository repository = new ReservationRepositoryImplementation(jdbi);

    var date = LocalDate.of(2023, 12, 1);

    repository.createReservation(LocalTime.of(12, 30, 0), LocalTime.of(13, 30, 0), date, 1L, 1L);
    repository.createReservation(LocalTime.of(15, 30, 0), LocalTime.of(16, 30, 0), date, 1L, 1L);
    repository.createReservation(LocalTime.of(17, 30, 0), LocalTime.of(18, 0, 0), date, 1L, 1L);

    List<ReservationRepository.ReservationEntity> reservations = repository.getAllForRoom(date, 1L);

    assertNotNull(reservations);
    assertEquals(3, reservations.size());
  }

  @Test
  @DisplayName("Should retrieve all reservations for a user")
  void shouldRetrieveAllReservationsForUser()
      throws ReservationExceptions.ReservationDatabaseException {
    ReservationRepository repository = new ReservationRepositoryImplementation(jdbi);

    var date = LocalDate.of(2023, 12, 1);

    repository.createReservation(LocalTime.of(12, 30, 0), LocalTime.of(13, 30, 0), date, 1L, 1L);
    repository.createReservation(LocalTime.of(15, 30, 0), LocalTime.of(16, 30, 0), date, 1L, 1L);

    List<ReservationRepository.ReservationEntity> reservations = repository.getAllForUser(1L);

    assertNotNull(reservations);
    assertEquals(2, reservations.size());
  }

  @Test
  @DisplayName("Should calculate total minutes for a user")
  void shouldCalculateTotalWeekMinutesForUser()
      throws ReservationExceptions.ReservationDatabaseException {
    ReservationRepository repository = new ReservationRepositoryImplementation(jdbi);

    repository.createReservation(
        LocalTime.of(12, 0, 0), LocalTime.of(13, 0, 0), LocalDate.of(2023, 11, 30), 1L, 1L);
    repository.createReservation(
        LocalTime.of(15, 0, 0), LocalTime.of(16, 0, 0), LocalDate.of(2023, 11, 30), 1L, 1L);
    repository.createReservation(
        LocalTime.of(12, 0, 0), LocalTime.of(13, 0, 0), LocalDate.of(2023, 12, 4), 1L, 1L);
    repository.createReservation(
        LocalTime.of(15, 0, 0), LocalTime.of(16, 0, 0), LocalDate.of(2023, 12, 5), 1L, 1L);

    var result =
        repository.getTotalMinutesForUser(
            1L, LocalDate.of(2023, 11, 30), LocalDate.of(2023, 12, 5));

    assertEquals(180, result);
  }

  @Test
  @DisplayName("Should retrieve reservation intersections for a room on a specific date")
  void shouldRetrieveReservationIntersectionsForRoomOnDate()
      throws ReservationExceptions.ReservationDatabaseException {
    ReservationRepository repository = new ReservationRepositoryImplementation(jdbi);

    repository.createReservation(
        LocalTime.of(14, 0, 0), LocalTime.of(15, 0, 0), LocalDate.of(2023, 12, 3), 1L, 1L);
    repository.createReservation(
        LocalTime.of(15, 0, 0), LocalTime.of(16, 0, 0), LocalDate.of(2023, 12, 3), 1L, 1L);
    repository.createReservation(
        LocalTime.of(12, 0, 0), LocalTime.of(13, 0, 0), LocalDate.of(2023, 12, 3), 1L, 1L);
    repository.createReservation(
        LocalTime.of(15, 0, 0), LocalTime.of(16, 0, 0), LocalDate.of(2023, 12, 4), 1L, 1L);

    int intersections =
        repository.getIntersections(
            LocalTime.of(11, 0, 0), LocalTime.of(17, 0, 0), LocalDate.of(2023, 12, 3), 1L);

    assertEquals(3, intersections);
  }

  @Test
  @DisplayName("Should create a new reservation")
  void shouldCreateNewReservation() throws ReservationExceptions.ReservationDatabaseException {
    ReservationRepository repository = new ReservationRepositoryImplementation(jdbi);
    LocalTime start = LocalTime.now();
    LocalTime end = start.plusHours(1);
    LocalDate startDay = LocalDate.now();
    Long userId = 1L;
    Long roomId = 1L;

    Long reservationId = repository.createReservation(start, end, startDay, userId, roomId);

    assertNotNull(reservationId);

    ReservationRepository.ReservationEntity createdReservation = repository.getById(reservationId);
    assertNotNull(createdReservation);
    assertEquals(start, createdReservation.start());
    assertEquals(end, createdReservation.end());
    assertEquals(startDay, createdReservation.startDay());
    assertEquals(userId, createdReservation.userId());
    assertEquals(roomId, createdReservation.roomId());
  }

  @Test
  @DisplayName("Should delete a reservation by ID")
  void shouldDeleteReservationById() throws ReservationExceptions.ReservationDatabaseException {
    ReservationRepository repository = new ReservationRepositoryImplementation(jdbi);
    long reservationId =
        repository.createReservation(
            LocalTime.now(), LocalTime.now().plusHours(1), LocalDate.now(), 1L, 1L);

    repository.deleteReservation(reservationId);

    var exception =
        assertThrows(
            ReservationExceptions.ReservationDatabaseException.class,
            () -> {
              repository.getById(reservationId);
            });

    assertEquals("Reservation not found", exception.getMessage());
  }
}
