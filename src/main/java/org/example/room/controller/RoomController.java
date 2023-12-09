package org.example.room.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controller.Controller;
import org.example.room.RoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  public void initializeEndpoints() {}

  // TODO: 12/9/23 findRoomById method

  // TODO: 12/9/23 createRoom method

  // TODO: 12/9/23 updateRoom method

  // TODO: 12/9/23 deleteRoom method
}
