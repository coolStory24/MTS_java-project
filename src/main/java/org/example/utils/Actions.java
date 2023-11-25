package org.example.utils;

import org.example.room.Room;
import org.example.room.RoomActions;
import org.example.user.User;
import org.example.user.UserActions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalDate.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Actions {
  public static ArrayList<InformationAboutReservation> list = new ArrayList<>();

  private static long countMinutes(LocalDateTime time) {
    int countDays = 0;
    for (int i = 1; i < time.getMonthValue(); i++) {
      countDays += LocalDate.of(time.getYear(), i, time.getDayOfMonth()).lengthOfMonth();
    }
    countDays += time.getDayOfMonth() - 1;
    return countDays * TimeUnit.DAYS.toMinutes(1)
        + time.getHour() * TimeUnit.HOURS.toMinutes(1)
        + time.getMinute();
  }

  private static boolean check(Room room, LocalDateTime start, LocalDateTime finish) {
    long startMinutes = countMinutes(start);
    long finishMinutes = countMinutes(finish);
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).idRoom != room.id) {
        continue;
      }
      long startInList = countMinutes(list.get(i).dateTimeStartReservation);
      long finishInList = countMinutes(list.get(i).dateTimeFinishReservation);
      if (startMinutes < startInList && startInList < finishMinutes) {
        return false;
      }
      if (startMinutes < finishInList && finishInList < finishMinutes) {
        return false;
      }
      if (startInList < startMinutes && startMinutes < finishInList) {
        return false;
      }
      if (startInList < finishMinutes && finishMinutes < finishInList) {
        return false;
      }
    }
    return true;
  }

  public static synchronized void reservation(
      User user, Room room, LocalDateTime start, LocalDateTime finish) {
    if (start.isBefore(LocalDateTime.now())) {
      System.out.println("Выберите время начала бронирования в будущем");
    }
    if ((start.getYear() < 2023 || finish.getYear() < 2023)
        || (start.getYear() > 2024 || finish.getYear() > 2024)) {
      System.out.println("\nГод начала и окончания бронирования должен быть 2023\n");
      return;
    }
    if (countMinutes(finish) - countMinutes(start) < 5) {
      System.out.println("\nМинимальное время бронирование - 5 минут\n");
      return;
    }
    if (countMinutes(start) < countMinutes(room.startInterval)
        || countMinutes(room.finishInterval) < countMinutes(finish)) {
      System.out.println("\nБронировать аудитории можно только в разрешённый интервал\n");
      return;
    }
    if (!check(room, start, finish)) {
      System.out.println(
          "\nПроизошло пересечение по бронированиям. Попробуйте ввести другое время\n");
      return;
    }
    InformationAboutReservation newInf =
        new InformationAboutReservation(user.id, room.id, start, finish);
    list.add(newInf);
  }

  public static LocalDateTime stringToTime(String str) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    LocalDateTime dateTime = LocalDateTime.parse(str, formatter);
    return dateTime;
  }

  public static synchronized void deleteReservation(
      User user, Room room, LocalDateTime start, LocalDateTime finish) {
    RoomActions.delete(user, room, start, finish);
    UserActions.delete(user, room, start, finish);
    System.out.println("\nУдаление прошло успешно\n");
  }
}
