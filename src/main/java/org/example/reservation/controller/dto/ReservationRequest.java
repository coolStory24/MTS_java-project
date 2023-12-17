package org.example.reservation.controller.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationRequest {
  public record CreateReservation(
      LocalTime start, LocalTime end, LocalDate startDay, long userId, long roomId) {}
}
