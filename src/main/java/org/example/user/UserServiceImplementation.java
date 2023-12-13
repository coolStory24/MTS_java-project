package org.example.user;

public class UserServiceImplementation implements UserService {
  private final UserRepository userRepository;

  public UserServiceImplementation(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserRepository.UserEntity findUserById(long id)
      throws UserExceptions.UserNotFoundException {
    try {
      return userRepository.getById(id);
    } catch (UserExceptions.UserNotFoundException e) {
      throw new UserExceptions.UserNotFoundException("Cannot find user", e);
    }
  }

  @Override
  public long createUser(String name) throws UserExceptions.UserCreateException {
    try {
      return userRepository.create(name);
    } catch (UserExceptions.UserCreateException e) {
      throw new UserExceptions.UserCreateException("Cannot create user", e);
    }
  }

  @Override
  public void updateUser(long id, String name) throws UserExceptions.UserUpdateException {
    try {
      userRepository.update(id, name);
    } catch (UserExceptions.UserUpdateException e) {
      throw new UserExceptions.UserUpdateException("Cannot update user", e);
    }
  }

  @Override
  public void deleteUser(long id) throws UserExceptions.UserDeleteException {
    try {
      userRepository.delete(id);
    } catch (UserExceptions.UserDeleteException e) {
      throw new UserExceptions.UserDeleteException("Cannot delete user", e);
    }
  }
}
