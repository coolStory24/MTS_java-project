package org.example.reservation;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.result.ResultBearing;

public class ReservationRepositoryImplementation implements ReservationRepository {
  private final Jdbi jdbi;

  public ReservationRepositoryImplementation(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public ReservationEntity getById(Long id)
      throws ReservationExceptions.ReservationDatabaseException {
    try {
      System.out.println();
      return jdbi.inTransaction(
          (Handle handle) -> {
            Map<String, Object> result =
                handle
                    .createQuery(
                        "SELECT r.id, r.user_id, r.start, r.\"end\", r.start_day, r.room_id "
                            + "FROM reservation r "
                            + "WHERE r.id = :id")
                    .bind("id", id)
                    .mapToMap()
                    .first();
            return new ReservationRepository.ReservationEntity(
                (Long) result.get("id"),
                ((Time) result.get("start")).toLocalTime(),
                ((Time) result.get("end")).toLocalTime(),
                ((java.sql.Date) result.get("start_day")).toLocalDate(),
                (Long) result.get("user_id"),
                (Long) result.get("room_id"));
          });
    } catch (Exception e) {
      throw new ReservationExceptions.ReservationDatabaseException("Reservation not found", e);
    }
  }

  @Override
  public List<ReservationEntity> getAllForRoom(LocalDate date, Long roomId)
      throws ReservationExceptions.ReservationDatabaseException {
    List<ReservationEntity> reservationEntities = new ArrayList<>();
    try {
      System.out.println();
      return jdbi.inTransaction(
          (Handle handle) -> {
            List<Map<String, Object>> results =
                handle
                    .createQuery(
                        "SELECT r.id, r.user_id, r.start, r.\"end\", r.start_day, r.room_id "
                            + "FROM reservation r "
                            + "WHERE r.room_id = :roomId AND r.start_day = :date")
                    .bind("roomId", roomId)
                    .bind("date", date)
                    .mapToMap()
                    .list();

            for (Map<String, Object> result : results) {
              reservationEntities.add(
                  new ReservationRepository.ReservationEntity(
                      (Long) result.get("id"),
                      ((Time) result.get("start")).toLocalTime(),
                      ((Time) result.get("end")).toLocalTime(),
                      ((java.sql.Date) result.get("start_day")).toLocalDate(),
                      (Long) result.get("user_id"),
                      (Long) result.get("room_id")));
            }

            return reservationEntities;
          });
    } catch (Exception e) {
      throw new ReservationExceptions.ReservationDatabaseException("Cannot find reservation", e);
    }
  }

  @Override
  public List<ReservationEntity> getAllForUser(Long userId)
      throws ReservationExceptions.ReservationDatabaseException {
    List<ReservationEntity> reservationEntities = new ArrayList<>();
    try {
      System.out.println();
      return jdbi.inTransaction(
          (Handle handle) -> {
            List<Map<String, Object>> results =
                handle
                    .createQuery(
                        "SELECT r.id, r.user_id, r.start, r.\"end\", r.start_day, r.room_id "
                            + "FROM reservation r "
                            + "WHERE r.user_id = :userId ")
                    .bind("userId", userId)
                    .mapToMap()
                    .list();

            for (Map<String, Object> result : results) {
              reservationEntities.add(
                  new ReservationRepository.ReservationEntity(
                      (Long) result.get("id"),
                      ((Time) result.get("start")).toLocalTime(),
                      ((Time) result.get("end")).toLocalTime(),
                      ((java.sql.Date) result.get("start_day")).toLocalDate(),
                      (Long) result.get("user_id"),
                      (Long) result.get("room_id")));
            }

            return reservationEntities;
          });
    } catch (Exception e) {
      throw new ReservationExceptions.ReservationDatabaseException("Cannot find reservation", e);
    }
  }

  @Override
  public int getTotalMinutesForUser(Long userId, LocalDate startPeriod, LocalDate endPeriod)
      throws ReservationExceptions.ReservationDatabaseException {
    try {
      System.out.println();
      return jdbi.inTransaction(
          (Handle handle) -> {
            Integer totalMinutes =
                handle
                    .createQuery(
                        "SELECT COALESCE(SUM(EXTRACT(EPOCH FROM r.\"end\" - r.start)/60), 0) as total_minutes "
                            + "FROM reservation r "
                            + "WHERE r.user_id = :userId AND r.start_day >= :startPeriod AND r.start_day < :endPeriod")
                    .bind("userId", userId)
                    .bind("startPeriod", startPeriod)
                    .bind("endPeriod", endPeriod)
                    .mapTo(Integer.class)
                    .one();

            return totalMinutes;
          });
    } catch (Exception e) {
      throw new ReservationExceptions.ReservationDatabaseException("Cannot find reservation", e);
    }
  }

  @Override
  public int getIntersections(LocalTime start, LocalTime end, LocalDate date, Long roomId)
      throws ReservationExceptions.ReservationDatabaseException {
    List<ReservationEntity> reservationEntities = new ArrayList<>();
    try {
      System.out.println();
      return jdbi.inTransaction(
          (Handle handle) -> {
            List<Map<String, Object>> results =
                handle
                    .createQuery(
                        "SELECT r.id, r.user_id, r.start, r.\"end\", r.start_day, r.room_id "
                            + "FROM reservation r "
                            + "WHERE r.room_id = :roomId AND r.start_day = :date AND NOT(:end <= r.start OR r.\"end\" <= :start)")
                    .bind("roomId", roomId)
                    .bind("date", date)
                    .bind("end", end)
                    .bind("start", start)
                    .mapToMap()
                    .list();

            for (Map<String, Object> result : results) {
              reservationEntities.add(
                  new ReservationRepository.ReservationEntity(
                      (Long) result.get("id"),
                      ((Time) result.get("start")).toLocalTime(),
                      ((Time) result.get("end")).toLocalTime(),
                      ((java.sql.Date) result.get("start_day")).toLocalDate(),
                      (Long) result.get("user_id"),
                      (Long) result.get("room_id")));
            }

            return reservationEntities.size();
          });
    } catch (Exception e) {
      throw new ReservationExceptions.ReservationDatabaseException("Cannot find intersections", e);
    }
  }

  @Override
  public Long createReservation(
      LocalTime start, LocalTime end, LocalDate startDay, Long userId, Long roomId)
      throws ReservationExceptions.ReservationDatabaseException {
    try {
      return jdbi.inTransaction(
          (Handle handle) -> {
            ResultBearing resultBearing =
                handle
                    .createUpdate(
                        "INSERT INTO reservation (user_id, start, \"end\", start_day, room_id) "
                            + "VALUES (:userId, :start, :end, :startDay, :roomId)")
                    .bind("userId", userId)
                    .bind("start", start)
                    .bind("end", end)
                    .bind("startDay", startDay)
                    .bind("roomId", roomId)
                    .executeAndReturnGeneratedKeys("id");
            Map<String, Object> mapResult = resultBearing.mapToMap().first();
            return ((Long) mapResult.get("id"));
          });
    } catch (Exception e) {
      throw new ReservationExceptions.ReservationDatabaseException("Cannot create reservation", e);
    }
  }

  @Override
  public void deleteReservation(Long reservationId)
      throws ReservationExceptions.ReservationDatabaseException {
    var rowsAffected =
        jdbi.inTransaction(
            (Handle handle) -> {
              return handle
                  .createUpdate("DELETE FROM reservation WHERE id = :reservationId")
                  .bind("reservationId", reservationId)
                  .execute();
            });
    if (rowsAffected == 0) {
      throw new ReservationExceptions.ReservationDatabaseException("Cannot delete reservation");
    }
  }
}
