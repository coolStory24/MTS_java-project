package org.example.reservation.controller.dto;

public class ReservationRequest {

  public record CreateReservation(
      String start, String end, String startDay, long userId, long roomId) {}
}
