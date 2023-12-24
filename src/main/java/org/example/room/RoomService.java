package org.example.room;

import java.time.LocalTime;

public interface RoomService {
  RoomRepository.RoomEntity getRoomById(long id) throws RoomExceptions.RoomNotFoundException;

  long createRoom(String title, LocalTime start, LocalTime end)
      throws RoomExceptions.RoomCreateException;

  void updateRoom(long id, String title, LocalTime start, LocalTime end)
      throws RoomExceptions.RoomUpdateException;

  void deleteRoom(long id) throws RoomExceptions.RoomDeleteException;
}
