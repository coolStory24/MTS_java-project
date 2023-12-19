package org.example.room;

import java.time.LocalTime;

public class RoomServiceImplementation implements RoomService {
  private final RoomRepository repository;

  public RoomServiceImplementation(RoomRepository repository) {
    this.repository = repository;
  }

  @Override
  public RoomRepository.RoomEntity getRoomById(long id)
      throws RoomExceptions.RoomNotFoundException {
    try {
      return this.repository.getById(id);
    } catch (RoomExceptions.RoomDatabaseException e) {
      throw new RoomExceptions.RoomNotFoundException("Room with id " + id + " not found", e);
    }
  }

  @Override
  public long createRoom(String title, LocalTime start, LocalTime end)
      throws RoomExceptions.RoomCreateException {
    try {
      return this.repository.create(title, start, end);
    } catch (RoomExceptions.RoomDatabaseException e) {
      throw new RoomExceptions.RoomCreateException("Cannot create room", e);
    }
  }

  @Override
  public void updateRoom(long id, String title, LocalTime start, LocalTime end)
      throws RoomExceptions.RoomUpdateException {
    try {
      this.repository.update(id, title, start, end);
    } catch (RoomExceptions.RoomDatabaseException e) {
      throw new RoomExceptions.RoomUpdateException("Cannot update room", e);
    }
  }

  @Override
  public void deleteRoom(long id) throws RoomExceptions.RoomDeleteException {
    try {
      this.repository.delete(id);
    } catch (RoomExceptions.RoomDatabaseException e) {
      throw new RoomExceptions.RoomDeleteException("Cannot delete room", e);
    }
  }
}
