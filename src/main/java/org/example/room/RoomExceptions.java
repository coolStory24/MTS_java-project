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
}
