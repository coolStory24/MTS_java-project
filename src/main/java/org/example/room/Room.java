package org.example.room;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;

public class Room {
  public long id;
  public String name;
  public LocalDateTime startInterval;
  public LocalDateTime finishInterval;

  public ArrayList<InformationAboutReservationRoom> informationList = new ArrayList<>();

  public Room(String name) {
    this.id = RoomActions.generateID();
    this.name = name;
    this.startInterval = LocalDateTime.of(2023, Month.JANUARY, 1, 0, 0);
    this.finishInterval = LocalDateTime.of(2023, Month.DECEMBER, 31, 23, 59);
  }
}
