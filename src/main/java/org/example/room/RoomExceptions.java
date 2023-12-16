package org.example.room;

public class RoomExceptions {
  public static class RoomDatabaseException extends RuntimeException {
    public RoomDatabaseException(String message) {
      super(message);
    }

    public RoomDatabaseException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException(String message) {
      super(message);
    }

    public RoomNotFoundException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static class RoomUpdateException extends RoomDatabaseException {
    public RoomUpdateException(String message) {
      super(message);
    }

    public RoomUpdateException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static class RoomDeleteException extends RoomDatabaseException {
    public RoomDeleteException(String message) {
      super(message);
    }

    public RoomDeleteException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static class RoomCreateException extends RoomDatabaseException {
    public RoomCreateException(String message) {
      super(message);
    }

    public RoomCreateException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
