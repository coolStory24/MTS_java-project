package org.example.reservation.controller.dto;

import org.example.reservation.ReservationRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class ReservationResponse {
  public record FindReservation(Long id, LocalTime start, LocalTime end, LocalDate startDay, Long userId, Long roomId) {
  }

  public record FindReservationsForUser(List<ReservationRepository.ReservationEntity> reservationsForUser) {
  }

  public record FindReservationsForRoom(List<ReservationRepository.ReservationEntity> reservationsForRoom) {
  }

  public record CreateReservation(long reservationId) {
  }
}
