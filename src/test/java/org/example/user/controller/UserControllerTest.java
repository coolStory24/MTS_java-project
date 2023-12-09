package org.example.user.controller;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.example.Application;
import org.example.user.UserExceptions;
import org.example.user.UserRepository;
import org.example.user.UserService;
import org.example.user.controller.dto.UserErrorResponse;
import org.example.user.controller.dto.UserResponse;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import spark.Service;

@DisplayName("User controller test")
class UserControllerTest {

  private Service service;

  @BeforeEach
  void beforeEach() {
    service = Service.ignite();
  }

  @AfterEach
  void afterEach() {
    service.stop();
    service.awaitStop();
  }

  @Test
  @DisplayName("Should 201 if user is successfully created")
  void should201IfUserIsSuccessfullyCreated() throws Exception {
    UserService userService = Mockito.mock(UserService.class);
    ObjectMapper objectMapper = new ObjectMapper();
    Application application =
        new Application(List.of(new UserController(service, objectMapper, userService)));
    Mockito.when(userService.createUser("Jack")).thenReturn(1L);
    application.start();
    service.awaitInitialization();

    HttpResponse<String> response =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .POST(
                        HttpRequest.BodyPublishers.ofString(
                            "{\"name\": \"Jack\"}"))
                    .uri(URI.create("http://localhost:%d/api/user".formatted(service.port())))
                    .build(),
                HttpResponse.BodyHandlers.ofString(UTF_8));

    assertEquals(201, response.statusCode());
    UserResponse.CreateUser userCreateResponse =
        objectMapper.readValue(response.body(), UserResponse.CreateUser.class);
    assertEquals(1L, userCreateResponse.id());
  }

  @Test
  @DisplayName("Should 200 if user is successfully found")
  void should200IfUserIsSuccessfullyFound() throws Exception {
    UserService userService = Mockito.mock(UserService.class);
    ObjectMapper objectMapper = new ObjectMapper();
    Application application =
        new Application(List.of(new UserController(service, objectMapper, userService)));

    var userId = 1L;
    var userName = "Joe Doe";
    Mockito.when(userService.findUserById(userId))
        .thenReturn(new UserRepository.UserEntity(userId, userName));

    application.start();
    service.awaitInitialization();

    HttpResponse<String> response =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .GET()
                    .uri(
                        URI.create(
                            "http://localhost:%d/api/user/%d".formatted(service.port(), userId)))
                    .build(),
                HttpResponse.BodyHandlers.ofString(UTF_8));

    assertEquals(200, response.statusCode());
    UserResponse.FindUser userFindResponse =
        objectMapper.readValue(response.body(), UserResponse.FindUser.class);
    assertEquals(userId, userFindResponse.id());
    assertEquals(userName, userFindResponse.name());
  }

  @Test
  @DisplayName("Should 404 if user is not found")
  void should404IfUserIsNotFound() throws Exception {
    UserService userService = Mockito.mock(UserService.class);
    ObjectMapper objectMapper = new ObjectMapper();
    Application application =
        new Application(List.of(new UserController(service, objectMapper, userService)));

    var userId = 1L;
    Mockito.when(userService.findUserById(userId))
        .thenThrow(new UserExceptions.UserNotFoundException("User with id 1 not found"));

    application.start();
    service.awaitInitialization();

    HttpResponse<String> response =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .GET()
                    .uri(
                        URI.create(
                            "http://localhost:%d/api/user/%d".formatted(service.port(), userId)))
                    .build(),
                HttpResponse.BodyHandlers.ofString(UTF_8));

    assertEquals(404, response.statusCode());
    UserErrorResponse userFindResponse =
        objectMapper.readValue(response.body(), UserErrorResponse.class);
    assertEquals("User with id 1 not found", userFindResponse.getMessage());
  }

  @Disabled("UserController methods must be implemented first")
  @Test
  @DisplayName("Should 200 if user is successfully updated")
  void should200IfUserIsSuccessfullyUpdated() throws IOException, InterruptedException {
    UserService userService = Mockito.mock(UserService.class);
    ObjectMapper objectMapper = new ObjectMapper();
    Application application =
        new Application(List.of(new UserController(service, objectMapper, userService)));

    var userId = 1L;
    Mockito.doNothing().when(userService).updateUser(1L, "John");

    application.start();
    service.awaitInitialization();

    HttpResponse<String> response =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .PUT(HttpRequest.BodyPublishers.ofString("{\"name\": \"John\"}"))
                    .uri(
                        URI.create(
                            "http://localhost:%d/api/user/%d".formatted(service.port(), userId)))
                    .build(),
                HttpResponse.BodyHandlers.ofString(UTF_8));

    Mockito.verify(userService, Mockito.times(1)).updateUser(1L, "John");
  }

  @Disabled("UserController methods must be implemented first")
  @Test
  @DisplayName("Should 204 if user is successfully deleted")
  void should204IfUserIsSuccessfullyDeleted() throws Exception {
    UserService userService = Mockito.mock(UserService.class);
    ObjectMapper objectMapper = new ObjectMapper();
    Application application =
        new Application(List.of(new UserController(service, objectMapper, userService)));

    var userId = 1L;

    application.start();
    service.awaitInitialization();

    HttpResponse<String> response =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .DELETE()
                    .uri(
                        URI.create(
                            "http://localhost:%d/api/user/%d".formatted(service.port(), userId)))
                    .build(),
                HttpResponse.BodyHandlers.ofString(UTF_8));

    assertEquals(204, response.statusCode());
  }

  @Disabled("UserController methods must be implemented first")
  @Test
  @DisplayName("Should 400 if update goes wrong")
  void should400IfUpdateFails() throws Exception {
    UserService userService = Mockito.mock(UserService.class);
    ObjectMapper objectMapper = new ObjectMapper();
    Application application =
        new Application(List.of(new UserController(service, objectMapper, userService)));

    var userId = 1L;
    Mockito.doThrow(new UserErrorResponse("Update failed for user with id 1"))
        .when(userService)
        .updateUser(userId, "John");

    application.start();
    service.awaitInitialization();

    HttpResponse<String> response =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .PUT(HttpRequest.BodyPublishers.ofString("{\"name\": \"John\"}"))
                    .uri(
                        URI.create(
                            "http://localhost:%d/api/user/%d".formatted(service.port(), userId)))
                    .build(),
                HttpResponse.BodyHandlers.ofString(UTF_8));

    assertEquals(400, response.statusCode());
    UserErrorResponse userErrorResponse =
        objectMapper.readValue(response.body(), UserErrorResponse.class);
    assertEquals("Update failed for user with id 1", userErrorResponse.getMessage());
  }

  @Disabled("UserController methods must be implemented first")
  @Test
  @DisplayName("Should 400 if deletion goes wrong")
  void should400IfDeletionFails() throws Exception {
    UserService userService = Mockito.mock(UserService.class);
    ObjectMapper objectMapper = new ObjectMapper();
    Application application =
        new Application(List.of(new UserController(service, objectMapper, userService)));

    var userId = 1L;
    Mockito.doThrow(new UserErrorResponse("Deletion failed for user with id 1"))
        .when(userService)
        .deleteUser(userId);

    application.start();
    service.awaitInitialization();

    HttpResponse<String> response =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .DELETE()
                    .uri(
                        URI.create(
                            "http://localhost:%d/api/user/%d".formatted(service.port(), userId)))
                    .build(),
                HttpResponse.BodyHandlers.ofString(UTF_8));

    assertEquals(400, response.statusCode());
    UserErrorResponse userErrorResponse =
        objectMapper.readValue(response.body(), UserErrorResponse.class);
    assertEquals("Deletion failed for user with id 1", userErrorResponse.getMessage());
  }
}
