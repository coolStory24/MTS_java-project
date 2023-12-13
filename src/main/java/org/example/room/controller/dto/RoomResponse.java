package org.example.room.controller.dto;

public class RoomResponse {
  public record CreateRoom(Long id) {}

  public record FindRoom(Long id, String title, String start, String end) {}

  public record UpdateRoom() {}
}
