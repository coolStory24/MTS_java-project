package org.example.reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.controller.Controller;
import org.example.reservation.ReservationService;
import org.example.user.controller.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Service;

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
  public void initializeEndpoints() {}
}
