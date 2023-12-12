package org.example.user;

public interface UserRepository {
  public record UserEntity(Long id, String name) {}

  UserEntity getById(long id) throws UserExceptions.UserDatabaseException;

  long create(String name) throws UserExceptions.UserDatabaseException;

  void delete(long id) throws UserExceptions.UserDatabaseException;

  void update(long id, String name) throws UserExceptions.UserDatabaseException;
}
