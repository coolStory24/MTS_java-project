package org.example;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.util.ArrayList;
import java.util.Scanner;
import org.example.room.Room;
import org.example.room.RoomActions;
import org.example.user.User;
import org.example.user.UserActions;
import org.example.utils.Actions;
import org.flywaydb.core.Flyway;

public class Main {
  public static void main(String[] args) {
    Config config = ConfigFactory.load();

    Flyway flyway =
        Flyway.configure()
            .outOfOrder(true)
            .locations("classpath:db/migrations")
            .dataSource(
                config.getString("app.database.url"),
                config.getString("app.database.user"),
                config.getString("app.database.password"))
            .load();
    flyway.migrate();

    Scanner input = new Scanner(System.in);
    ArrayList<User> users = new ArrayList<>();
    ArrayList<Room> rooms = new ArrayList<>();
    while (true) {
      System.out.println(
          "Введите одно из действий: \n"
              + "1 - Создать пользователя\n"
              + "2 - Создать аудиторию\n"
              + "3 - Поменять настройки аудитории\n"
              + "4 - Забронировать аудиторию на выбранные день и время\n"
              + "5 - Отменить бронирование\n");
      String requestNumber = input.next();
      if (requestNumber.equals("1")) {
        System.out.print("Введите имя пользователя: ");
        String userName = input.next();
        UserActions.newUser(users, userName);
      } else if (requestNumber.equals("2")) {
        System.out.print("Введите название аудитории: ");
        String roomName = input.next();
        RoomActions.newRoom(rooms, roomName);
      } else if (requestNumber.equals("3")) {
        System.out.println("Что именно Вы хотите поменять (имя/интервал)?");
        String update = input.next();
        if (update.equals("имя")) {
          System.out.print("Введите старое и новое имя аудитории (через пробел): ");
          String oldName = input.next();
          String newName = input.next();
          Room room = RoomActions.findRoom(rooms, oldName);
          RoomActions.rename(room, newName);
        }
        if (update.equals("интервал")) {
          System.out.print(
              "Введите имя аудитории и границы нового интервала в формате " + "yyyy-MM-dd HH:mm: ");
          String nameRoom = input.next();
          String newStartInterval = input.next();
          String newFinishInterval = input.next();
          Room room = RoomActions.findRoom(rooms, nameRoom);
          RoomActions.updateInterval(
              room,
              Actions.stringToTime(newStartInterval),
              Actions.stringToTime(newFinishInterval));
        }
      } else if (requestNumber.equals("4")) {
        System.out.print(
            "Введите имя пользователя, название аудитории и границы "
                + "бронирования в формате yyyy-MM-dd HH:mm: ");
        String nameUser = input.next();
        String nameRoom = input.next();
        String timeStart = input.next();
        String timeFinish = input.next();
        User user = UserActions.findUsers(users, nameUser);
        Room room = RoomActions.findRoom(rooms, nameRoom);
        Actions.reservation(
            user, room, Actions.stringToTime(timeStart), Actions.stringToTime(timeFinish));
      } else if (requestNumber.equals("5")) {
        System.out.print(
            "Введите имя пользователя, название аудитории и границы бронирования в формате yyyy-MM-dd HH:mm: ");
        String nameUser = input.next();
        String nameRoom = input.next();
        String timeStart = input.next();
        String timeFinish = input.next();
        User user = UserActions.findUsers(users, nameUser);
        Room room = RoomActions.findRoom(rooms, nameRoom);
        Actions.deleteReservation(
            user, room, Actions.stringToTime(timeStart), Actions.stringToTime(timeFinish));
      } else {
        System.out.println("Было введено некорректное значение. Попробуйте ещё раз");
      }
    }
  }
}
