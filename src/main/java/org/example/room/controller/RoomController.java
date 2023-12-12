package org.example.room.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.http.HttpStatus;
import org.example.controller.Controller;
import org.example.room.RoomExceptions;
import org.example.room.RoomService;
import org.example.room.controller.dto.RoomErrorResponse;
import org.example.room.controller.dto.RoomRequest;
import org.example.room.controller.dto.RoomResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

public class RoomController implements Controller {
  private static final Logger LOG = LoggerFactory.getLogger(RoomController.class);
  private final Service service;
  private final ObjectMapper objectMapper;
  private final RoomService roomService;

  public RoomController(Service service, ObjectMapper objectMapper, RoomService roomService) {
    this.service = service;
    this.objectMapper = objectMapper;
    this.roomService = roomService;
  }

  @Override
  public void initializeEndpoints() {
    createRoom();
    updateRoom();
    findRoomById();
    deleteRoom();
  }

  private void createRoom() {
    service.post(
        "/api/room",
        (Request request, Response response) -> {
          response.type("application/json");

          String body = request.body();

          RoomRequest.CreateRoom addRoomRequest =
              objectMapper.readValue(body, RoomRequest.CreateRoom.class);

          try {
            var roomId =
                roomService.createRoom(
                    addRoomRequest.title(), addRoomRequest.start(), addRoomRequest.end());

            response.status(HttpStatus.CREATED_201);
            LOG.debug("Room successfully added");
            return objectMapper.writeValueAsString(new RoomResponse.CreateRoom(roomId));
          } catch (RoomExceptions.RoomCreateException e) {
            LOG.warn("Cannot create a new room", e);
            response.status(HttpStatus.BAD_REQUEST_400);
            return objectMapper.writeValueAsString(new RoomErrorResponse(e.getMessage()));
          } catch (RuntimeException e) {
            LOG.error("Unhandled error", e);
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            return objectMapper.writeValueAsString(new RoomErrorResponse("Internal server error"));
          }
        });
  }

  private void updateRoom() {
    service.put(
        "/api/room/:id",
        (Request request, Response response) -> {
          response.type("application/json");
          String id = request.params("id");
          String body = request.body();

          RoomRequest.UpdateRoom updateRoomRequest =
              objectMapper.readValue(body, RoomRequest.UpdateRoom.class);

          try {
            var roomId = Long.parseLong(id);

            roomService.updateRoom(
                roomId,
                updateRoomRequest.title(),
                updateRoomRequest.start(),
                updateRoomRequest.end());

            response.status(HttpStatus.OK_200);
            LOG.debug("Room successfully updated");
            return objectMapper.writeValueAsString(new RoomResponse.UpdateRoom());
          } catch (RoomExceptions.RoomUpdateException e) {
            LOG.warn("Cannot update the room with id: " + id, e);
            response.status(HttpStatus.BAD_REQUEST_400);
            return objectMapper.writeValueAsString(new RoomErrorResponse(e.getMessage()));
          } catch (RuntimeException e) {
            LOG.error("Unhandled error", e);
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            return objectMapper.writeValueAsString(new RoomErrorResponse("Internal server error"));
          }
        });
  }

  private void findRoomById() {
    service.get(
        "/api/room/:id",
        (Request request, Response response) -> {
          response.type("application/json");
          String id = request.params("id");

          try {
            var roomId = Long.parseLong(id);

            var room = roomService.getRoomById(roomId);
            response.status(HttpStatus.OK_200);
            LOG.debug("Room successfully found");
            return objectMapper.writeValueAsString(
                new RoomResponse.FindRoom(
                    room.id(), room.title(), room.startInterval(), room.endInterval()));
          } catch (RoomExceptions.RoomNotFoundException e) {
            LOG.warn("Cannot find the room with id: " + id, e);
            response.status(HttpStatus.NOT_FOUND_404);
            return objectMapper.writeValueAsString(new RoomErrorResponse(e.getMessage()));
          } catch (RuntimeException e) {
            LOG.error("Unhandled error", e);
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            return objectMapper.writeValueAsString(new RoomErrorResponse("Internal server error"));
          }
        });
  }

  private void deleteRoom() {
    service.delete(
        "/api/room/:id",
        (Request request, Response response) -> {
          response.type("application/json");
          String id = request.params("id");

          try {
            var roomId = Long.parseLong(id);
            roomService.deleteRoom(roomId);

            response.status(HttpStatus.OK_200);
            LOG.debug("Room successfully deleted");
            return objectMapper.writeValueAsString(new RoomResponse.DeleteRoom());
          } catch (RoomExceptions.RoomDeleteException e) {
            LOG.warn("Cannot delete the room with id: " + id, e);
            response.status(HttpStatus.BAD_REQUEST_400);
            return objectMapper.writeValueAsString(new RoomErrorResponse(e.getMessage()));
          } catch (RuntimeException e) {
            LOG.error("Unhandled error", e);
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            return objectMapper.writeValueAsString(new RoomErrorResponse("Internal server error"));
          }
        });
  }
}
