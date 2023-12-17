package org.example.reservation.controller.dto;

public class ReservationErrorResponse extends RuntimeException {
  public ReservationErrorResponse(String message) {super(message);}
}
