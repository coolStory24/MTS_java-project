package org.example.reservation.controller.dto;

import java.util.List;

public class ReservationResponse {
  public record FindReservation(
      Long id, String start, String end, String startDay, Long userId, Long roomId) {}

  public record FindReservationsForUser(List<FindReservation> reservationsForUser) {}

  public record FindReservationsForRoom(List<FindReservation> reservationsForRoom) {}

  public record CreateReservation(long reservationId) {}
}
