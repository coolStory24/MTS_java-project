package org.example.room;

public class RoomServiceImplementation implements RoomService {

  private final RoomRepository roomRepository;

  public RoomServiceImplementation(RoomRepository roomRepository) {
    this.roomRepository = roomRepository;
  }

  @Override
  public RoomRepository.RoomEntity getRoomById(long id)
      throws RoomExceptions.RoomNotFoundException {
    try {
      return roomRepository.getById(id);
    } catch (RoomExceptions.RoomNotFoundException e) {
      throw new RoomExceptions.RoomNotFoundException("Cannot find room", e);
    }
  }

  @Override
  public long createRoom(String title, String start, String end)
      throws RoomExceptions.RoomCreateException {
    try {
      return roomRepository.create(title, start, end);
    } catch (RoomExceptions.RoomCreateException e) {
      throw new RoomExceptions.RoomCreateException("Cannot create room", e);
    }
  }

  @Override
  public void updateRoom(long id, String title, String start, String end)
      throws RoomExceptions.RoomUpdateException {
    try {
      roomRepository.update(id, title, start, end);
    } catch (RoomExceptions.RoomUpdateException e) {
      throw new RoomExceptions.RoomUpdateException("Cannot update room");
    }
  }

  @Override
  public void deleteRoom(long id) throws RoomExceptions.RoomDeleteException {
    try {
      roomRepository.delete(id);
    } catch (RoomExceptions.RoomDeleteException e) {
      throw new RoomExceptions.RoomDeleteException("Cannot delete room", e);
    }
  }
}
