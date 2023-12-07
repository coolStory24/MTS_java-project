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

  public static class UserNotFoundException extends UserDatabaseException {
    public UserNotFoundException(String message) {
      super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static class UserCreateException extends UserDatabaseException {
    public UserCreateException(String message) {
      super(message);
    }

    public UserCreateException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static class UserUpdateException extends UserDatabaseException {
    public UserUpdateException(String message) {
      super(message);
    }

    public UserUpdateException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static class UserDeleteException extends UserDatabaseException {
    public UserDeleteException(String message) {
      super(message);
    }

    public UserDeleteException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
