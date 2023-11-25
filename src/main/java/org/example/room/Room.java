package org.example.room;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.concurrent.atomic.AtomicLong;

public class Room {
  public AtomicLong id;
  public String name;
  public LocalDateTime startInterval;
  public LocalDateTime finishInterval;

  public Room(String name) {
    this.id = RoomActions.generateID();
    this.name = name;
    this.startInterval = LocalDateTime.of(2023, Month.JANUARY, 1, 0, 0);
    this.finishInterval = LocalDateTime.of(2023, Month.DECEMBER, 31, 23, 59);
  }
}
