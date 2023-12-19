package org.example.room;

import java.time.LocalTime;

public interface RoomRepository {
  record RoomEntity(Long id, String title, LocalTime startInterval, LocalTime endInterval) {}

  RoomEntity getById(long id) throws RoomExceptions.RoomDatabaseException;

  long create(String title, LocalTime start, LocalTime end)
      throws RoomExceptions.RoomDatabaseException;

  void delete(long id);

  void update(long id, String title, LocalTime start, LocalTime end)
      throws RoomExceptions.RoomDatabaseException;
}
