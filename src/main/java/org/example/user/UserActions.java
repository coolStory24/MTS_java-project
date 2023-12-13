package org.example.user;

import org.example.room.Room;
import org.example.utils.Actions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class UserActions {
  private static AtomicLong id;

  public static AtomicLong generateID() {
    id.incrementAndGet();
    return id;
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
    for (int i = 0; i < Actions.list.size(); i++) {
      if (Actions.list.get(i).idUser != user.id) continue;
      if (Actions.list.get(i).idRoom == room.id
          && Actions.list.get(i).dateTimeStartReservation.equals(start)
          && Actions.list.get(i).dateTimeFinishReservation.equals(finish)) {
        Actions.list.get(i).idRoom = null;
        Actions.list.get(i).dateTimeStartReservation = null;
        Actions.list.get(i).dateTimeFinishReservation = null;
      }
    }
  }
}
