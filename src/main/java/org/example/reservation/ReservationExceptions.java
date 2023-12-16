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
}
