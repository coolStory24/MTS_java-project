package org.example.user;

public class UserExceptions {
  public static class UserDatabaseException extends RuntimeException {
    public UserDatabaseException(String message) {
      super(message);
    }

    public UserDatabaseException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
