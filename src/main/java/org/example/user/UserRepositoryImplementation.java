package org.example.user;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.result.ResultBearing;
import java.util.Map;

public class UserRepositoryImplementation implements UserRepository {
  private final Jdbi jdbi;

  public UserRepositoryImplementation(Jdbi jdbi) throws UserExceptions.UserDatabaseException {
    this.jdbi = jdbi;
  }

  @Override
  public UserEntity getById(long id) throws UserExceptions.UserDatabaseException {
    try {
      return jdbi.inTransaction(
          (Handle handle) -> {
            Map<String, Object> result =
                handle
                    .createQuery("SELECT u.id, u.name " + "FROM \"user\" u " + "WHERE u.id = :id")
                    .bind("id", id)
                    .mapToMap()
                    .first();
            return new UserRepository.UserEntity(
                (Long) result.get("id"), (String) result.get("name"));
          });

    } catch (Exception e) {
      throw new UserExceptions.UserDatabaseException("Cannot find user", e);
    }
  }

  @Override
  public long create(String name) throws UserExceptions.UserDatabaseException {
    try {
      return jdbi.inTransaction(
          (Handle handle) -> {
            ResultBearing resultBearing =
                handle
                    .createUpdate("INSERT INTO \"user\" (name) " + "VALUES (:nameUser)")
                    .bind("nameUser", name)
                    .executeAndReturnGeneratedKeys("id");
            Map<String, Object> mapResult = resultBearing.mapToMap().first();
            return ((Long) mapResult.get("id"));
          });
    } catch (Exception e) {
      throw new UserExceptions.UserDatabaseException("Cannot create user", e);
    }
  }

  @Override
  public void delete(long id) throws UserExceptions.UserDatabaseException {
    var countChange =
        jdbi.inTransaction(
            (Handle handle) -> {
              return handle
                  .createUpdate("DELETE FROM \"user\" WHERE id = :id")
                  .bind("id", id)
                  .execute();
            });
    if (countChange == 0) {
      throw new UserExceptions.UserDatabaseException("Cannot delete user");
    }
  }

  @Override
  public void update(long id, String name) throws UserExceptions.UserDatabaseException {
    try {
      jdbi.useTransaction(
          (Handle handle) -> {
            handle
                .createUpdate("UPDATE \"user\" SET name = :nameUser WHERE id = :id ")
                .bind("id", id)
                .bind("nameUser", name)
                .execute();
          });
    } catch (Exception e) {
      throw new UserExceptions.UserDatabaseException("Cannot update user", e);
    }
  }
}
