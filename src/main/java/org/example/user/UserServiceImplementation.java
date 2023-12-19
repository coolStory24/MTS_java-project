package org.example.user;

public class UserServiceImplementation implements UserService {
  private final UserRepository repository;

  public UserServiceImplementation(UserRepository repository) {
    this.repository = repository;
  }

  @Override
  public UserRepository.UserEntity findUserById(long id)
      throws UserExceptions.UserNotFoundException {
    try {
      return this.repository.getById(id);
    } catch (UserExceptions.UserDatabaseException e) {
      throw new UserExceptions.UserNotFoundException("Cannot find user with id " + id, e);
    }
  }

  @Override
  public long createUser(String name) throws UserExceptions.UserCreateException {
    try {
      return this.repository.create(name);
    } catch (UserExceptions.UserDatabaseException e) {
      throw new UserExceptions.UserCreateException("Cannot create user", e);
    }
  }

  @Override
  public void updateUser(long id, String name) throws UserExceptions.UserUpdateException {
    try {
      this.repository.update(id, name);
    } catch (UserExceptions.UserDatabaseException e) {
      throw new UserExceptions.UserUpdateException("Cannot update user", e);
    }
  }

  @Override
  public void deleteUser(long id) throws UserExceptions.UserDeleteException {
    try {
      this.repository.delete(id);
    } catch (UserExceptions.UserDatabaseException e) {
      throw new UserExceptions.UserDeleteException("Cannot delete user", e);
    }
  }
}
