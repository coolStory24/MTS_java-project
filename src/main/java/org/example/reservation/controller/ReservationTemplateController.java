package org.example.reservation.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.example.controller.Controller;
import org.example.reservation.ReservationRepository;
import org.example.reservation.ReservationService;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Service;
import spark.template.freemarker.FreeMarkerEngine;

public class ReservationTemplateController implements Controller {
  private final Service service;
  private final ReservationService reservationService;
  private final FreeMarkerEngine freeMarkerEngine;

  public ReservationTemplateController(
      Service service, ReservationService reservationService, FreeMarkerEngine freeMarkerEngine) {
    this.service = service;
    this.reservationService = reservationService;
    this.freeMarkerEngine = freeMarkerEngine;
  }

  @Override
  public void initializeEndpoints() {
    getAllArticles();
  }

  private void getAllArticles() {
    service.get(
        "/api/:id",
        (Request request, Response response) -> {
          response.type("text/html; charset=utf-8");
          var id = request.params("id");
          var date = request.queryParams("date");

          List<ReservationRepository.ReservationEntity> reservations =
              reservationService.getAllReservationsForRoom(
                  LocalDate.parse(date), Long.parseLong(id));
          List<Map<String, String>> reservationMapList =
              reservations.stream()
                  .map(
                      reservation ->
                          Map.of(
                              "start", reservation.start().toString(),
                              "end", reservation.end().toString(),
                              "userId", reservation.userId().toString()))
                  .toList();

          Map<String, Object> model = new HashMap<>();
          model.put("reservations", reservationMapList);
          model.put("roomId", id);
          model.put(
              "date",
              LocalDate.parse(date)
                  .format(DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("ru"))));
          return freeMarkerEngine.render(new ModelAndView(model, "index.ftl"));
        });
  }
}
