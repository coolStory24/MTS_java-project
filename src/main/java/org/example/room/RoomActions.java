package org.example.room;

import org.example.user.User;
import org.example.utils.Actions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class RoomActions {
  private static AtomicLong id;

  public static AtomicLong generateID() {
    id.incrementAndGet();
    return id;
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

  public static void newRoom(ArrayList<Room> rooms, String name) {
    Room room = new Room(name);
    rooms.add(room);
    System.out.println("\nАудитория успешно создана\n");
  }

  public static void delete(User user, Room room, LocalDateTime start, LocalDateTime finish) {
    for (int i = 0; i < Actions.list.size(); i++) {
      if (Actions.list.get(i).idRoom != room.id) continue;
      if (Actions.list.get(i).idUser == user.id
          && Actions.list.get(i).dateTimeStartReservation.equals(start)
          && Actions.list.get(i).dateTimeFinishReservation.equals(finish)) {
        Actions.list.get(i).idUser = null;
        Actions.list.get(i).dateTimeStartReservation = null;
        Actions.list.get(i).dateTimeFinishReservation = null;
      }
    }
  }
}
