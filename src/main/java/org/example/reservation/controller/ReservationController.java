package org.example.reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.http.HttpStatus;
import org.example.controller.Controller;
import org.example.reservation.ReservationExceptions;
import org.example.reservation.ReservationService;
import org.example.reservation.controller.dto.ReservationErrorResponse;
import org.example.reservation.controller.dto.ReservationRequest;
import org.example.reservation.controller.dto.ReservationResponse;
import org.example.room.RoomExceptions;
import org.example.user.UserExceptions;
import org.example.user.controller.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class ReservationController implements Controller {
  private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
  private final Service service;
  private final ObjectMapper objectMapper;
  private final ReservationService reservationService;

  public ReservationController(
      Service service, ObjectMapper objectMapper, ReservationService reservationService) {
    this.service = service;
    this.objectMapper = objectMapper;
    this.reservationService = reservationService;
  }

  @Override
  public void initializeEndpoints() {
    findReservationById();
    getAllReservationsForUser();
    getAllReservationsForRoom();
    createReservation();
    deleteReservation();
  }

  public void findReservationById() {
    service.get(
        "/api/reservation/:id",
        (Request request, Response response) -> {
          response.type("application/json");
          String id = request.params("id");

          try {
            var reservationId = Long.parseLong(id);

            var reservation = reservationService.findReservationById(reservationId);
            response.status(HttpStatus.OK_200);
            LOG.debug("Reservation successfully found");
            return objectMapper.writeValueAsString(
                new ReservationResponse.FindReservation(
                    reservationId,
                    reservation.start().toString(),
                    reservation.end().toString(),
                    reservation.startDay().toString(),
                    reservation.userId(),
                    reservation.roomId()));
          } catch (ReservationExceptions.ReservationNotFoundException e) {
            LOG.warn("Cannot find the reservation with id: " + id, e);
            response.status(HttpStatus.NOT_FOUND_404);
            return objectMapper.writeValueAsString(new ReservationErrorResponse(e.getMessage()));
          } catch (RuntimeException e) {
            LOG.error("Unhandled error", e);
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            return objectMapper.writeValueAsString(
                new ReservationErrorResponse("Internal server error"));
          }
        });
  }

  public void getAllReservationsForUser() {
    service.get(
        "/api/reservation/user/:id",
        (Request request, Response response) -> {
          response.type("application/json");
          String id = request.params("id");

          try {
            var userId = Long.parseLong(id);

            var reservationsForUser =
                reservationService.getAllReservationsForUser(userId).stream()
                    .map(
                        reservation ->
                            new ReservationResponse.FindReservation(
                                reservation.id(),
                                reservation.start().toString(),
                                reservation.end().toString(),
                                reservation.startDay().toString(),
                                reservation.userId(),
                                reservation.roomId()))
                    .collect(Collectors.toList());
            response.status(HttpStatus.OK_200);
            LOG.debug("Reservations for user are successfully found");
            return objectMapper.writeValueAsString(
                new ReservationResponse.FindReservationsForUser(reservationsForUser));
          } catch (UserExceptions.UserNotFoundException e) {
            LOG.warn("Cannot find the user with id: " + id, e);
            response.status(HttpStatus.NOT_FOUND_404);
            return objectMapper.writeValueAsString(new ReservationErrorResponse(e.getMessage()));
          } catch (RuntimeException e) {
            LOG.error("Unhandled error", e);
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            return objectMapper.writeValueAsString(
                new ReservationErrorResponse("Internal server error"));
          }
        });
  }

  public void getAllReservationsForRoom() {
    service.get(
        "/api/reservation/room/:id/date/:date",
        (Request request, Response response) -> {
          response.type("application/json");
          String id = request.params("id");
          String onDate = request.params("date");

          try {
            var roomId = Long.parseLong(id);
            var date = LocalDate.parse(onDate);

            var reservationsForRoom =
                reservationService.getAllReservationsForRoom(date, roomId).stream()
                    .map(
                        reservation ->
                            new ReservationResponse.FindReservation(
                                reservation.id(),
                                reservation.start().toString(),
                                reservation.end().toString(),
                                reservation.startDay().toString(),
                                reservation.userId(),
                                reservation.roomId()))
                    .collect(Collectors.toList());
            response.status(HttpStatus.OK_200);
            LOG.debug("Reservations for room on this date are successfully found");
            return objectMapper.writeValueAsString(
                new ReservationResponse.FindReservationsForRoom(reservationsForRoom));
          } catch (RoomExceptions.RoomNotFoundException e) {
            LOG.warn("Cannot find the room with id: " + id, e);
            response.status(HttpStatus.NOT_FOUND_404);
            return objectMapper.writeValueAsString(new ReservationErrorResponse(e.getMessage()));
          } catch (RuntimeException e) {
            LOG.error("Unhandled error", e);
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            return objectMapper.writeValueAsString(
                new ReservationErrorResponse("Internal server error"));
          }
        });
  }

  public void createReservation() {
    service.post(
        "/api/reservation",
        (Request request, Response response) -> {
          response.type("application/json");

          String body = request.body();

          ReservationRequest.CreateReservation createReservationRequest =
              objectMapper.readValue(body, ReservationRequest.CreateReservation.class);

          try {
            DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("HH:mm:ss");
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            var reservationId =
                reservationService.createReservation(
                    LocalTime.parse(createReservationRequest.start(), formatter1),
                    LocalTime.parse(createReservationRequest.end(), formatter1),
                    LocalDate.parse(createReservationRequest.startDay(), formatter2),
                    createReservationRequest.userId(),
                    createReservationRequest.roomId());

            response.status(HttpStatus.CREATED_201);
            LOG.debug("Reservation successfully added");
            return objectMapper.writeValueAsString(
                new ReservationResponse.CreateReservation(reservationId));
          } catch (ReservationExceptions.ReservationCreateException e) {
            LOG.warn("Cannot create a new reservation", e);
            response.status(HttpStatus.BAD_REQUEST_400);
            return objectMapper.writeValueAsString(new ReservationErrorResponse(e.getMessage()));
          } catch (RuntimeException e) {
            LOG.error("Unhandled error", e);
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            return objectMapper.writeValueAsString(
                new ReservationErrorResponse("Internal server error"));
          }
        });
  }

  public void deleteReservation() {
    service.delete(
        "/api/reservation/:id",
        (Request request, Response response) -> {
          response.type("application/json");
          String id = request.params("id");

          try {
            var reservationId = Long.parseLong(id);
            reservationService.deleteReservation(reservationId);

            response.status(HttpStatus.NO_CONTENT_204);
            LOG.debug("Reservation successfully deleted");
            return "";
          } catch (ReservationExceptions.ReservationDeleteException e) {
            LOG.warn("Cannot delete the reservations with id: " + id, e);
            response.status(HttpStatus.BAD_REQUEST_400);
            return objectMapper.writeValueAsString(new ReservationErrorResponse(e.getMessage()));
          } catch (RuntimeException e) {
            LOG.error("Unhandled error", e);
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            return objectMapper.writeValueAsString(
                new ReservationErrorResponse("Internal server error"));
          }
        });
  }
}
