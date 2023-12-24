package org.example.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservationRepository {
  record ReservationEntity(
      Long id, LocalTime start, LocalTime end, LocalDate startDay, Long userId, Long roomId) {}

  ReservationEntity getById(Long id) throws ReservationExceptions.ReservationDatabaseException;

  List<ReservationEntity> getAllForRoom(LocalDate date, Long roomId)
      throws ReservationExceptions.ReservationDatabaseException;

  List<ReservationEntity> getAllForUser(Long userId)
      throws ReservationExceptions.ReservationDatabaseException;

  int getTotalMinutesForUser(Long userId, LocalDate startPeriod, LocalDate endPeriod)
      throws ReservationExceptions.ReservationDatabaseException;

  int getIntersections(LocalTime start, LocalTime end, LocalDate date, Long roomId)
      throws ReservationExceptions.ReservationDatabaseException;

  Long createReservation(
      LocalTime start, LocalTime end, LocalDate startDay, Long userId, Long roomId)
      throws ReservationExceptions.ReservationDatabaseException;

  void deleteReservation(Long reservationId)
      throws ReservationExceptions.ReservationDatabaseException;
}
