package org.example.room.controller.dto;

public class RoomErrorResponse extends RuntimeException {
  public RoomErrorResponse(String message) {
    super(message);
  }
}
