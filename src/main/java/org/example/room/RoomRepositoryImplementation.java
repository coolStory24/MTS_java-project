package org.example.room;

import java.time.LocalTime;
import java.util.Map;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.result.ResultBearing;

public class RoomRepositoryImplementation implements RoomRepository {
  private final Jdbi jdbi;

  public RoomRepositoryImplementation(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public RoomEntity getById(long id) throws RoomExceptions.RoomDatabaseException {
    try {
      return jdbi.inTransaction(
          (Handle handle) -> {
            Map<String, Object> result =
                handle
                    .createQuery(
                        "SELECT r.id, r.title, r.start_interval, r.end_interval "
                            + "FROM room r "
                            + "WHERE r.id = :id")
                    .bind("id", id)
                    .mapToMap()
                    .first();
            return new RoomEntity(
                (Long) result.get("id"),
                (String) result.get("title"),
                result.get("start_interval".toLowerCase()).toString(),
                result.get("end_interval".toLowerCase()).toString());
          });
    } catch (Exception e) {
      throw new RoomExceptions.RoomDatabaseException("Cannot find room", e);
    }
  }

  @Override
  public long create(String title, String start, String end)
      throws RoomExceptions.RoomDatabaseException {
    try {
      return jdbi.inTransaction(
          (Handle handle) -> {
            ResultBearing resultBearing =
                handle
                    .createUpdate(
                        "INSERT INTO room (title, start_interval, end_interval) "
                            + "VALUES (:title, :start, :end)")
                    .bind("title", title)
                    .bind("start", LocalTime.parse(start))
                    .bind("end", LocalTime.parse(end))
                    .executeAndReturnGeneratedKeys("id");
            Map<String, Object> mapResult = resultBearing.mapToMap().first();
            return ((Long) mapResult.get("id"));
          });
    } catch (Exception e) {
      throw new RoomExceptions.RoomDatabaseException("Cannot create room", e);
    }
  }

  @Override
  public void delete(long id) throws RoomExceptions.RoomDatabaseException {
    var rowsAffected =
        jdbi.inTransaction(
            (Handle handle) -> {
              return handle
                  .createUpdate("DELETE FROM room WHERE id = :id")
                  .bind("id", id)
                  .execute();
            });
    if (rowsAffected == 0) {
      throw new RoomExceptions.RoomDatabaseException("Cannot delete room");
    }
  }

  @Override
  public void update(long id, String title, String start, String end)
      throws RoomExceptions.RoomDatabaseException {
    try {
      var rowsAffected =
          jdbi.inTransaction(
              (Handle handle) -> {
                return handle
                    .createUpdate(
                        "UPDATE room SET title = :title, start_interval = :start, end_interval = :end WHERE id = :id ")
                    .bind("id", id)
                    .bind("title", title)
                    .bind("start", LocalTime.parse(start))
                    .bind("end", LocalTime.parse(end))
                    .execute();
              });
      if (rowsAffected == 0) {
        throw new RoomExceptions.RoomDatabaseException("Cannot update room");
      }
    } catch (Exception e) {
      throw new RoomExceptions.RoomDatabaseException("Cannot update room", e);
    }
  }
}
