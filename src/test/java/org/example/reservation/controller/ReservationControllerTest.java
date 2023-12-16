package org.example.reservation.controller;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.example.Application;
import org.example.controller.ReservationResponse;
import org.example.reservation.ReservationRepository;
import org.example.reservation.ReservationService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import spark.Service;

@Disabled("Controller methods must be implemented first")
@DisplayName("Reservation controller test")
class ReservationControllerTest {
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
  @DisplayName("Should 201 if reservation is successfully created")
  void should201IfRoomIsSuccessfullyCreated() throws Exception {
    ReservationService reservationService = Mockito.mock(ReservationService.class);
    ObjectMapper objectMapper = new ObjectMapper();
    Application application =
        new Application(
            List.of(new ReservationController(service, objectMapper, reservationService)));
    Mockito.when(
            reservationService.createReservation(
                LocalTime.of(12, 0, 0), LocalTime.of(14, 0, 0), LocalDate.of(2023, 12, 1), 1L, 1L))
        .thenReturn(1L);
    application.start();
    service.awaitInitialization();

    HttpResponse<String> response =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .POST(
                        HttpRequest.BodyPublishers.ofString(
                            "{\"start\":\"12:00:00\",\"end\":\"14:00:00\",\"day\":\"2023-12-01\", \"userId\": 1, \"roomId\": 1}"))
                    .uri(
                        URI.create("http://localhost:%d/api/reservation".formatted(service.port())))
                    .build(),
                HttpResponse.BodyHandlers.ofString(UTF_8));

    assertEquals(201, response.statusCode());
    ReservationResponse.CreateReservation reservationCreateResponse =
        objectMapper.readValue(response.body(), ReservationResponse.CreateReservation.class);
    assertEquals(1L, reservationCreateResponse.id());
  }

  @Test
  @DisplayName("Should return reservation by ID if it exists")
  void shouldReturnReservationByIdIfExists() throws Exception {
    ReservationService reservationService = Mockito.mock(ReservationService.class);
    ObjectMapper objectMapper = new ObjectMapper();
    Application application =
        new Application(
            List.of(new ReservationController(service, objectMapper, reservationService)));
    Mockito.when(reservationService.findReservationById(1L))
        .thenReturn(
            new ReservationRepository.ReservationEntity(
                1L,
                LocalTime.of(12, 0, 0),
                LocalTime.of(14, 0, 0),
                LocalDate.of(2023, 12, 1),
                1L,
                1L));
    application.start();
    service.awaitInitialization();

    HttpResponse<String> response =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .GET()
                    .uri(
                        URI.create(
                            "http://localhost:%d/api/reservation/1".formatted(service.port())))
                    .build(),
                HttpResponse.BodyHandlers.ofString(UTF_8));

    assertEquals(200, response.statusCode());
    ReservationResponse.FindReservation reservationFindResponse =
        objectMapper.readValue(response.body(), ReservationResponse.FindReservation.class);
    assertEquals("12:00:00", reservationFindResponse.start());
    assertEquals("14:00:00", reservationFindResponse.end());
    assertEquals("2023-12-01", reservationFindResponse.day());
  }

  @Test
  @DisplayName("Should return all reservations for a user")
  void shouldReturnAllReservationsForUser() throws Exception {
    ReservationService reservationService = Mockito.mock(ReservationService.class);
    ObjectMapper objectMapper = new ObjectMapper();
    Application application =
        new Application(
            List.of(new ReservationController(service, objectMapper, reservationService)));
    long userId = 1L;
    Mockito.when(reservationService.getAllReservationsForUser(userId))
        .thenReturn(
            List.of(
                new ReservationRepository.ReservationEntity(
                    1L,
                    LocalTime.of(12, 0, 0),
                    LocalTime.of(14, 0, 0),
                    LocalDate.of(2023, 12, 1),
                    1L,
                    1L)));
    application.start();
    service.awaitInitialization();

    HttpResponse<String> response =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .GET()
                    .uri(
                        URI.create(
                            "http://localhost:%d/api/reservation/user/%d"
                                .formatted(service.port(), userId)))
                    .build(),
                HttpResponse.BodyHandlers.ofString(UTF_8));

    assertEquals(200, response.statusCode());

    var reservationFindResponse =
        objectMapper.readValue(
            response.body(), new TypeReference<List<ReservationResponse.FindReservation>>() {});

    var firstReservation = reservationFindResponse.get(0);

    assertEquals(200, response.statusCode());

    assertEquals("12:00:00", firstReservation.start());
    assertEquals("14:00:00", firstReservation.end());
    assertEquals("2023-12-01", firstReservation.day());
  }

  @Test
  @DisplayName("Should return all reservations for a room on a specific date")
  void shouldReturnAllReservationsForRoomOnDate() throws Exception {
    ReservationService reservationService = Mockito.mock(ReservationService.class);
    ObjectMapper objectMapper = new ObjectMapper();
    Application application =
        new Application(
            List.of(new ReservationController(service, objectMapper, reservationService)));
    long roomId = 1L;
    LocalDate date = LocalDate.of(2023, 12, 1);
    Mockito.when(reservationService.getAllReservationsForRoom(date, roomId))
        .thenReturn(
            List.of(
                new ReservationRepository.ReservationEntity(
                    1L,
                    LocalTime.of(12, 0, 0),
                    LocalTime.of(14, 0, 0),
                    LocalDate.of(2023, 12, 1),
                    1L,
                    1L)));

    application.start();
    service.awaitInitialization();

    HttpResponse<String> response =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .GET()
                    .uri(
                        URI.create(
                            "http://localhost:%d/api/reservation/room/%d/date/%s"
                                .formatted(service.port(), roomId, date.toString())))
                    .build(),
                HttpResponse.BodyHandlers.ofString(UTF_8));

    assertEquals(200, response.statusCode());

    var reservationFindResponse =
        objectMapper.readValue(
            response.body(), new TypeReference<List<ReservationResponse.FindReservation>>() {});

    var firstReservation = reservationFindResponse.get(0);

    assertEquals(200, response.statusCode());

    assertEquals("12:00:00", firstReservation.start());
    assertEquals("14:00:00", firstReservation.end());
    assertEquals("2023-12-01", firstReservation.day());
  }

  @Test
  @DisplayName("Should delete reservation by ID if it exists")
  void shouldDeleteReservationByIdIfExists() throws Exception {
    ReservationService reservationService = Mockito.mock(ReservationService.class);
    ObjectMapper objectMapper = new ObjectMapper();
    Application application =
        new Application(
            List.of(new ReservationController(service, objectMapper, reservationService)));
    long reservationId = 1L;
    application.start();
    service.awaitInitialization();

    HttpResponse<String> response =
        HttpClient.newHttpClient()
            .send(
                HttpRequest.newBuilder()
                    .DELETE()
                    .uri(
                        URI.create(
                            "http://localhost:%d/api/reservation/%d"
                                .formatted(service.port(), reservationId)))
                    .build(),
                HttpResponse.BodyHandlers.ofString(UTF_8));

    assertEquals(204, response.statusCode());
  }
}
