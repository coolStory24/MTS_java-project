package org.example.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.example.room.RoomExceptions;
import org.example.user.UserExceptions;

public interface ReservationService {
  ReservationRepository.ReservationEntity findReservationById(long id)
      throws ReservationExceptions.ReservationNotFoundException;

  List<ReservationRepository.ReservationEntity> getAllReservationsForUser(long userId)
      throws UserExceptions.UserNotFoundException;

  List<ReservationRepository.ReservationEntity> getAllReservationsForRoom(
      LocalDate date, long roomId) throws RoomExceptions.RoomNotFoundException;

  long createReservation(
      LocalTime start, LocalTime end, LocalDate startDay, long userId, long roomId)
      throws ReservationExceptions.ReservationCreateException;

  void deleteReservation(long reservationId)
      throws ReservationExceptions.ReservationDeleteException;
}
