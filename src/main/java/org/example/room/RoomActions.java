package org.example.room;

import org.example.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class RoomActions {
  private static long id = 0;

  public static long generateID() {
    return ++id;
  }

  public static void rename(Room room, String newName) {
    room.name = newName;
    System.out.println("Имя аудитории успешно обновлено");
    System.out.println("");
  }

  public static void updateInterval(Room room, LocalDateTime newStart, LocalDateTime newFinish) {
    room.startInterval = newStart;
    room.finishInterval = newFinish;
    System.out.println("Интервал аудитории успешно обновлён");
    System.out.println("");
  }

  public static Room findRoom(ArrayList<Room> rooms, String name) {
    Room room = null;
    for (int i = 0; i < rooms.size(); i++) {
      if (rooms.get(i).name.equals(name)) {
        room = rooms.get(i);
      }
    }
    return room;
  }

  public static void newRoom (ArrayList<Room> rooms, String name) {
    Room room = new Room(name);
    rooms.add(room);
    System.out.println("\nАудитория успешно создана\n");
  }

  public static void delete(User user, Room room, LocalDateTime start, LocalDateTime finish) {
    for (int i = 0; i < room.informationList.size(); i++) {
      if (room.informationList.get(i).idUser == user.id
          && room.informationList.get(i).dateTimeStartReservation.equals(start)
          && room.informationList.get(i).dateTimeFinishReservation.equals(finish)) {
        room.informationList.get(i).idUser = -1;
        room.informationList.get(i).dateTimeStartReservation = null;
        room.informationList.get(i).dateTimeFinishReservation = null;
      }
    }
  }
}
