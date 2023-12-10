package org.example.user.controller.dto;

public class UserRequest {
  public record CreateUser(String name) {}

  public record UpdateUser(String name) {}
}
