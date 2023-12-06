package org.example.user;

import org.jdbi.v3.core.Jdbi;

public class UserRepositoryImplementation implements UserRepository {
  private final Jdbi jdbi;

  public UserRepositoryImplementation(Jdbi jdbi) throws UserExceptions.UserDatabaseException {
    this.jdbi = jdbi;
  }

  @Override
  public UserEntity getById(long id) throws UserExceptions.UserDatabaseException {
    // TODO: 12/6/23 getById method
    return null;
  }

  @Override
  public long create(String name) throws UserExceptions.UserDatabaseException {
    // TODO: 12/6/23 create method
    return 0;
  }

  @Override
  public void delete(long id) throws UserExceptions.UserDatabaseException {
    // TODO: 12/6/23 delete method
  }

  @Override
  public void update(long id, String name) throws UserExceptions.UserDatabaseException {
    // TODO: 12/6/23 update method
  }
}
