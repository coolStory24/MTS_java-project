package org.example.room.controller;

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
import org.example.room.RoomExceptions;
import org.example.room.RoomRepository;
import org.example.room.RoomService;
import org.example.room.controller.dto.RoomErrorResponse;
import org.example.room.controller.dto.RoomResponse;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import spark.Service;

@Disabled("RoomController methods must be implemented first")
@DisplayName("Room controller test")
class RoomControllerTest {

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
  @DisplayName("Should 201 if room is successfully created")
  void should201IfRoomIsSuccessfullyCreated() throws Exception {
    RoomService roomService = Mockito.mock(RoomService.class);
    ObjectMapper objectMapper = new ObjectMapper();
    Application application =
        new Application(List.of(new RoomController(service, objectMapper, roomService)));
    Mockito.when(roomService.createRoom("Room1", "09:00:00", "20:00:00")).thenReturn(1L);
    application.start();
    service.awaitInitialization();

    HttpResponse<String> response =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .POST(
                        HttpRequest.BodyPublishers.ofString(
                            """
                              {
                                "name": "Room1",
                                "start": "09:00:00",
                                "end": "20:00:00"
                              }"""))
                    .uri(URI.create("http://localhost:%d/api/room".formatted(service.port())))
                    .build(),
                HttpResponse.BodyHandlers.ofString(UTF_8));

    assertEquals(201, response.statusCode());
    RoomResponse.CreateRoom roomCreateResponse =
        objectMapper.readValue(response.body(), RoomResponse.CreateRoom.class);
    assertEquals(1L, roomCreateResponse.id());
  }

  @Test
  @DisplayName("Should 200 if room is successfully found")
  void should200IfRoomIsSuccessfullyFound() throws Exception {
    RoomService roomService = Mockito.mock(RoomService.class);
    ObjectMapper objectMapper = new ObjectMapper();
    Application application =
        new Application(List.of(new RoomController(service, objectMapper, roomService)));

    var roomId = 1L;
    var roomName = "Conference Room";
    var roomStart = "00:00:00";
    var roomEnd = "22:00:00";
    Mockito.when(roomService.getRoomById(roomId))
        .thenReturn(new RoomRepository.RoomEntity(roomId, roomName, roomStart, roomEnd));

    application.start();
    service.awaitInitialization();

    HttpResponse<String> response =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .GET()
                    .uri(
                        URI.create(
                            "http://localhost:%d/api/room/%d".formatted(service.port(), roomId)))
                    .build(),
                HttpResponse.BodyHandlers.ofString(UTF_8));

    assertEquals(200, response.statusCode());
    RoomResponse.FindRoom roomFindResponse =
        objectMapper.readValue(response.body(), RoomResponse.FindRoom.class);
    assertEquals(roomId, roomFindResponse.id());
    assertEquals(roomName, roomFindResponse.title());
    assertEquals(roomStart, roomFindResponse.start());
    assertEquals(roomName, roomFindResponse.title());
  }

  @Test
  @DisplayName("Should 404 if room is not found")
  void should404IfRoomIsNotFound() throws Exception {
    RoomService roomService = Mockito.mock(RoomService.class);
    ObjectMapper objectMapper = new ObjectMapper();
    Application application =
        new Application(List.of(new RoomController(service, objectMapper, roomService)));

    var roomId = 1L;
    Mockito.when(roomService.getRoomById(roomId))
        .thenThrow(new RoomExceptions.RoomNotFoundException("Room with id 1 not found"));

    application.start();
    service.awaitInitialization();

    HttpResponse<String> response =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .GET()
                    .uri(
                        URI.create(
                            "http://localhost:%d/api/room/%d".formatted(service.port(), roomId)))
                    .build(),
                HttpResponse.BodyHandlers.ofString(UTF_8));

    assertEquals(404, response.statusCode());
    RoomErrorResponse roomFindResponse =
        objectMapper.readValue(response.body(), RoomErrorResponse.class);
    assertEquals("Room with id 1 not found", roomFindResponse.getMessage());
  }

  @Test
  @DisplayName("Should 200 if room is successfully updated")
  void should200IfRoomIsSuccessfullyUpdated() throws IOException, InterruptedException {
    RoomService roomService = Mockito.mock(RoomService.class);
    ObjectMapper objectMapper = new ObjectMapper();
    Application application =
        new Application(List.of(new RoomController(service, objectMapper, roomService)));

    var roomId = 1L;
    Mockito.doNothing().when(roomService).updateRoom(1L, "Updated Room", "05:00:00", "09:00:00");

    application.start();
    service.awaitInitialization();

    HttpResponse<String> response =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .PUT(
                        HttpRequest.BodyPublishers.ofString(
                            """
                          {
                              "name": "Updated Room",
                              "start: "05:00:00",
                              "end": "09:00:00"
                          }"""))
                    .uri(
                        URI.create(
                            "http://localhost:%d/api/room/%d".formatted(service.port(), roomId)))
                    .build(),
                HttpResponse.BodyHandlers.ofString(UTF_8));

    Mockito.verify(roomService, Mockito.times(1))
        .updateRoom(1L, "Updated Room", "05:00:00", "09:00:00");
  }

  @Test
  @DisplayName("Should 204 if room is successfully deleted")
  void should204IfRoomIsSuccessfullyDeleted() throws Exception {
    RoomService roomService = Mockito.mock(RoomService.class);
    ObjectMapper objectMapper = new ObjectMapper();
    Application application =
        new Application(List.of(new RoomController(service, objectMapper, roomService)));

    var roomId = 1L;

    application.start();
    service.awaitInitialization();

    HttpResponse<String> response =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .DELETE()
                    .uri(
                        URI.create(
                            "http://localhost:%d/api/room/%d".formatted(service.port(), roomId)))
                    .build(),
                HttpResponse.BodyHandlers.ofString(UTF_8));

    assertEquals(204, response.statusCode());
  }

  @Test
  @DisplayName("Should 400 if update goes wrong")
  void should400IfUpdateFails() throws Exception {
    RoomService roomService = Mockito.mock(RoomService.class);
    ObjectMapper objectMapper = new ObjectMapper();
    Application application =
        new Application(List.of(new RoomController(service, objectMapper, roomService)));

    var roomId = 1L;
    Mockito.doThrow(new RoomErrorResponse("Update failed for room with id 1"))
        .when(roomService)
        .updateRoom(roomId, "Invalid Room", "05:00:00", "09:00:00");

    application.start();
    service.awaitInitialization();

    HttpResponse<String> response =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .PUT(
                        HttpRequest.BodyPublishers.ofString(
                            """
                        {
                            "name": "Invalid Room",
                            "start: "06:00:00",
                            "end": "10:00:00"
                        }"""))
                    .uri(
                        URI.create(
                            "http://localhost:%d/api/room/%d".formatted(service.port(), roomId)))
                    .build(),
                HttpResponse.BodyHandlers.ofString(UTF_8));

    assertEquals(400, response.statusCode());
    RoomErrorResponse roomErrorResponse =
        objectMapper.readValue(response.body(), RoomErrorResponse.class);
    assertEquals("Update failed for room with id 1", roomErrorResponse.getMessage());
  }

  @Test
  @DisplayName("Should 400 if deletion goes wrong")
  void should400IfDeletionFails() throws Exception {
    RoomService roomService = Mockito.mock(RoomService.class);
    ObjectMapper objectMapper = new ObjectMapper();
    Application application =
        new Application(List.of(new RoomController(service, objectMapper, roomService)));

    var roomId = 1L;
    Mockito.doThrow(new RoomErrorResponse("Deletion failed for room with id 1"))
        .when(roomService)
        .deleteRoom(roomId);

    application.start();
    service.awaitInitialization();

    HttpResponse<String> response =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .DELETE()
                    .uri(
                        URI.create(
                            "http://localhost:%d/api/room/%d".formatted(service.port(), roomId)))
                    .build(),
                HttpResponse.BodyHandlers.ofString(UTF_8));

    assertEquals(400, response.statusCode());
    RoomErrorResponse roomErrorResponse =
        objectMapper.readValue(response.body(), RoomErrorResponse.class);
    assertEquals("Deletion failed for room with id 1", roomErrorResponse.getMessage());
  }
}
