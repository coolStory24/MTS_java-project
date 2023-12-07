package org.example.room;

public interface RoomService {
  RoomRepository.RoomEntity getById(long id) throws RoomExceptions.RoomNotFoundException;

  long create(String title, String start, String end) throws RoomExceptions.RoomCreateException;

  void update(long id, String title, String start, String end)
      throws RoomExceptions.RoomUpdateException;

  void delete(long id) throws RoomExceptions.RoomDeleteException;
}
