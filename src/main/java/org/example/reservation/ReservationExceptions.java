package org.example.reservation;

public class ReservationExceptions {
  public static class ReservationDatabaseException extends RuntimeException {
    public ReservationDatabaseException(String message) {
      super(message);
    }

    public ReservationDatabaseException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static class ReservationNotFoundException extends RuntimeException {
    public ReservationNotFoundException(String message) {
      super(message);
    }

    public ReservationNotFoundException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static class ReservationUpdateException extends ReservationDatabaseException {
    public ReservationUpdateException(String message) {
      super(message);
    }

    public ReservationUpdateException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static class ReservationDeleteException extends ReservationDatabaseException {
    public ReservationDeleteException(String message) {
      super(message);
    }

    public ReservationDeleteException(String message, Throwable cause) {
      super(message, cause);
    }
  }

  public static class ReservationCreateException extends ReservationDatabaseException {
    public ReservationCreateException(String message) {
      super(message);
    }

    public ReservationCreateException(String message, Throwable cause) {
      super(message, cause);
    }
  }
}
