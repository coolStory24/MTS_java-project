package org.example.room;

public interface RoomService {
  RoomRepository.RoomEntity getRoomById(long id) throws RoomExceptions.RoomNotFoundException;

  long createRoom(String title, String start, String end) throws RoomExceptions.RoomCreateException;

  void updateRoom(long id, String title, String start, String end)
      throws RoomExceptions.RoomUpdateException;

  void deleteRoom(long id) throws RoomExceptions.RoomDeleteException;
}
