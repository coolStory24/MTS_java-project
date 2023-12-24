package org.example.reservation;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.example.room.RoomExceptions;
import org.example.room.RoomRepository;
import org.example.transaction.TransactionManager;
import org.example.user.UserExceptions;
import org.example.utils.DateUtils;

public class ReservationServiceImplementation implements ReservationService {
  private final TransactionManager transactionManager;
  private final ReservationRepository reservationRepository;
  private final RoomRepository roomRepository;

  public ReservationServiceImplementation(
      ReservationRepository reservationRepository,
      RoomRepository roomRepository,
      TransactionManager transactionManager) {
    this.transactionManager = transactionManager;
    this.reservationRepository = reservationRepository;
    this.roomRepository = roomRepository;
  }

  @Override
  public ReservationRepository.ReservationEntity findReservationById(long id)
      throws ReservationExceptions.ReservationNotFoundException {
    try {
      return this.reservationRepository.getById(id);
    } catch (ReservationExceptions.ReservationDatabaseException e) {
      throw new ReservationExceptions.ReservationNotFoundException(
          "Бронирование с id " + id + " не найдено", e);
    }
  }

  @Override
  public List<ReservationRepository.ReservationEntity> getAllReservationsForUser(long userId)
      throws UserExceptions.UserNotFoundException {
    return this.reservationRepository.getAllForUser(userId);
  }

  @Override
  public List<ReservationRepository.ReservationEntity> getAllReservationsForRoom(
      LocalDate date, long roomId) throws RoomExceptions.RoomNotFoundException {
    return this.reservationRepository.getAllForRoom(date, roomId);
  }

  @Override
  public synchronized long createReservation(
      LocalTime start, LocalTime end, LocalDate startDay, long userId, long roomId)
      throws ReservationExceptions.ReservationCreateException {

    var duration = Duration.between(start, end).toMinutes();

    if (duration < 5)
      throw new ReservationExceptions.ReservationCreateException(
          "Время бронирования должно составлять хотя бы 5 минут");

    try {
      var room = this.roomRepository.getById(roomId);

      if (start.isBefore(room.startInterval()) || end.isAfter(room.endInterval()))
        throw new ReservationExceptions.ReservationCreateException(
            "Промежуток бронирования недоступен для данной комнаты");

      var reservationId =
          transactionManager.inTransaction(
              () -> {
                var allowedMinutes = 180;
                var weekMonday = DateUtils.getMondayOfWeek(startDay);

                var minutesForWeek =
                    reservationRepository.getTotalMinutesForUser(
                        userId, weekMonday, weekMonday.plusWeeks(1));
                var minutesForPreviousWeek =
                    reservationRepository.getTotalMinutesForUser(
                        userId, weekMonday.minusWeeks(1), weekMonday);
                var minutesForPrePreviousWeek =
                    reservationRepository.getTotalMinutesForUser(
                        userId, weekMonday.minusWeeks(2), weekMonday.minusWeeks(1));

                if (minutesForPreviousWeek >= 180 && minutesForPrePreviousWeek >= 180) {
                  allowedMinutes = 480;
                }

                if (minutesForWeek + duration > allowedMinutes)
                  throw new ReservationExceptions.ReservationCreateException(
                      "Превышен недельный лимит времени бронирования");

                var intersections =
                    this.reservationRepository.getIntersections(start, end, startDay, roomId);

                if (intersections > 0)
                  throw new ReservationExceptions.ReservationCreateException(
                      "Есть пересечения с другими бронированиями");

                return this.reservationRepository.createReservation(
                    start, end, startDay, userId, roomId);
              });
      return reservationId;
    } catch (RoomExceptions.RoomNotFoundException e) {
      throw new ReservationExceptions.ReservationCreateException("Комната не найдена");
    }
  }

  @Override
  public void deleteReservation(long reservationId)
      throws ReservationExceptions.ReservationDeleteException {
    try {
      this.reservationRepository.deleteReservation(reservationId);
    } catch (ReservationExceptions.ReservationDatabaseException e) {
      throw new ReservationExceptions.ReservationDeleteException(
          "Не получилось удалить бронирование с id " + reservationId, e);
    }
  }
}
