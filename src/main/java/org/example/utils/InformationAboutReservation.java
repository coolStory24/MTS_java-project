package org.example.utils;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public class InformationAboutReservation {
  public static AtomicLong id;

  public AtomicLong idUser;
  public AtomicLong idRoom;
  public LocalDateTime dateTimeStartReservation;
  public LocalDateTime dateTimeFinishReservation;

  public InformationAboutReservation(
      AtomicLong idUser,
      AtomicLong idRoom,
      LocalDateTime dateTimeStartReservation,
      LocalDateTime dateTimeFinishReservation) {
    this.idUser = idUser;
    this.idRoom = idRoom;
    this.dateTimeStartReservation = dateTimeStartReservation;
    this.dateTimeFinishReservation = dateTimeFinishReservation;
    this.id = generateID();
  }

  private AtomicLong generateID() {
    id.incrementAndGet();
    return id;
  }
}
