package org.example.room;

public interface RoomRepository {
  public Room getById(long id);
  public long create(Room room);
  public void delete(long id);
  public void update(long id);
}
