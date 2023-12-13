package org.example.room;

public interface RoomRepository {
  record RoomEntity(Long id, String title, String startInterval, String endInterval) {}

  RoomEntity getById(long id) throws RoomExceptions.RoomDatabaseException;

  long create(String title, String start, String end) throws RoomExceptions.RoomDatabaseException;

  void delete(long id);

  void update(long id, String title, String start, String end)
      throws RoomExceptions.RoomDatabaseException;
}
