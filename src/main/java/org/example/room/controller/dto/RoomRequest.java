package org.example.room.controller.dto;

public class RoomRequest {
  public record CreateRoom(String title, String start, String end) {}

  public record UpdateRoom(String title, String start, String end) {}
}
