package org.example.user.controller.dto;

public class UserResponse {
  public record CreateUser(Long id) {}

  public record FindUser(Long id, String name) {}
}
