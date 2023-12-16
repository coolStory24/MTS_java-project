package org.example.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.jdbi.v3.core.Jdbi;

public class ReservationRepositoryImplementation implements ReservationRepository {
  private final Jdbi jdbi;

  public ReservationRepositoryImplementation(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public ReservationEntity getById(Long id)
      throws ReservationExceptions.ReservationDatabaseException {
    // TODO: Implement logic to retrieve a reservation by ID from the database
    return null;
  }

  @Override
  public List<ReservationEntity> getAllForRoom(LocalDate date, Long roomId)
      throws ReservationExceptions.ReservationDatabaseException {
    // TODO: Implement logic to retrieve all reservations for a room on a specific date from the
    // database
    return null;
  }

  @Override
  public List<ReservationEntity> getAllForUser(Long userId)
      throws ReservationExceptions.ReservationDatabaseException {
    // TODO: Implement logic to retrieve all reservations for a user from the database
    return null;
  }

  @Override
  public int getTotalMinutesForUser(Long userId, LocalDate startPeriod, LocalDate endPeriod)
      throws ReservationExceptions.ReservationDatabaseException {
    // TODO: Implement logic to calculate the total reservation minutes for a user in a specific
    // period [---)
    return 0;
  }

  @Override
  public int getIntersections(LocalTime start, LocalTime end, LocalDate date, Long roomId)
      throws ReservationExceptions.ReservationDatabaseException {
    // TODO: Implement logic to check for reservation time intersections for a room on a specific
    // date
    return 0;
  }

  @Override
  public Long createReservation(
      LocalTime start, LocalTime end, LocalDate startDay, Long userId, Long roomId)
      throws ReservationExceptions.ReservationDatabaseException {
    // TODO: Implement logic to create a new reservation in the database
    return null;
  }

  @Override
  public void deleteReservation(Long reservationId)
      throws ReservationExceptions.ReservationDatabaseException {
    // TODO: Implement logic to delete a reservation by ID from the database
  }
}
