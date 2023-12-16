package org.example.controller;

public class ReservationResponse {
  public record CreateReservation(Long id) {}

  public record FindReservation(
      Long id, String start, String end, String day, Long userId, Long roomId) {}
}
