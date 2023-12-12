package org.example.user.controller.dto;

public class UserErrorResponse extends RuntimeException {
  public UserErrorResponse(String message) {
    super(message);
  }
}
