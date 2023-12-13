package org.example.user;

public interface UserService {
  UserRepository.UserEntity findUserById(long id) throws UserExceptions.UserNotFoundException;

  long createUser(String name) throws UserExceptions.UserCreateException;

  void updateUser(long id, String name) throws UserExceptions.UserUpdateException;

  void deleteUser(long id) throws UserExceptions.UserDeleteException;
}
