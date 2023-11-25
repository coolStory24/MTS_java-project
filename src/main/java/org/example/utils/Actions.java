package org.example.utils;

import org.example.room.InformationAboutReservationRoom;
import org.example.room.Room;
import org.example.room.RoomActions;
import org.example.user.InformationAboutReservationUser;
import org.example.user.User;
import org.example.user.UserActions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Actions {
  private static long countMinutes(LocalDateTime time) {
    int[] countDaysInEveryMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    int countDays = 0;
    for (int i = 0; i < time.getMonthValue() - 1; i++) {
      countDays += countDaysInEveryMonth[i];
    }
    countDays += time.getDayOfMonth() - 1;
    return countDays * 1440 + time.getHour() * 60 + time.getMinute();
  }

  private static boolean check(Room room, LocalDateTime start, LocalDateTime finish) {
    long startMinutes = countMinutes(start);
    long finishMinutes = countMinutes(finish);
    for (int i = 0; i < room.informationList.size(); i++) {
      long startInList = countMinutes(room.informationList.get(i).dateTimeStartReservation);
      long finishInList = countMinutes(room.informationList.get(i).dateTimeFinishReservation);
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
    if (start.getYear() != 2023 || finish.getYear() != 2023) {
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
    InformationAboutReservationRoom newInfRoom = new InformationAboutReservationRoom();
    newInfRoom.idUser = user.id;
    newInfRoom.dateTimeStartReservation = start;
    newInfRoom.dateTimeFinishReservation = finish;
    room.informationList.add(newInfRoom);

    InformationAboutReservationUser newInfUser = new InformationAboutReservationUser();
    newInfUser.idRoom = room.id;
    newInfUser.dateTimeStartReservation = start;
    newInfUser.dateTimeFinishReservation = finish;
    user.informationList.add(newInfUser);
    System.out.println("\nБронирование успешно создано\n");
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
