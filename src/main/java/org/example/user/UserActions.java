package org.example.user;

import org.example.room.Room;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class UserActions {
  private static long id = 0;

  public static long generateID() {
    return ++id;
  }

  public static User findUsers(ArrayList<User> users, String name) {
    User user = null;
    for (int i = 0; i < users.size(); i++) {
      if (users.get(i).name.equals(name)) {
        user = users.get(i);
      }
    }
    return user;
  }

  public static void newUser(ArrayList<User> users, String name) {
    User user = new User(name);
    users.add(user);
    System.out.println("\nПользователь успешно создан\n");
  }

  public static void delete(User user, Room room, LocalDateTime start, LocalDateTime finish) {
    for (int i = 0; i < user.informationList.size(); i++) {
      if (user.informationList.get(i).idRoom == room.id
          && user.informationList.get(i).dateTimeStartReservation.equals(start)
          && user.informationList.get(i).dateTimeFinishReservation.equals(finish)) {
        user.informationList.get(i).idRoom = -1;
        user.informationList.get(i).dateTimeStartReservation = null;
        user.informationList.get(i).dateTimeFinishReservation = null;
      }
    }
  }
}
