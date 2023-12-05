package org.example.user;

public interface UserRepository {
  public interface RoomRepository {
    public User getById(long id);
    public long create(User user);
    public void delete(long id);
    public void update(long id);
  }
}
