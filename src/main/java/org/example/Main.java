package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import java.util.List;
import org.example.reservation.ReservationRepositoryImplementation;
import org.example.reservation.ReservationServiceImplementation;
import org.example.reservation.controller.ReservationController;
import org.example.reservation.controller.ReservationTemplateController;
import org.example.room.RoomRepositoryImplementation;
import org.example.room.RoomServiceImplementation;
import org.example.room.controller.RoomController;
import org.example.template.TemplateFactory;
import org.example.transaction.JdbiTransactionManager;
import org.example.transaction.TransactionManager;
import org.example.user.UserRepositoryImplementation;
import org.example.user.UserServiceImplementation;
import org.example.user.controller.UserController;
import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import spark.Service;

public class Main {
  public static void main(String[] args) {
    Config config = ConfigFactory.load();

    Flyway flyway =
        Flyway.configure()
            .outOfOrder(true)
            .locations("classpath:db/migrations")
            .dataSource(
                config.getString("app.database.url"),
                config.getString("app.database.user"),
                config.getString("app.database.password"))
            .load();
    flyway.migrate();

    Jdbi jdbi =
        Jdbi.create(
            config.getString("app.database.url"),
            config.getString("app.database.user"),
            config.getString("app.database.password"));


    Service service = Service.ignite();

    Application application = getApplication(jdbi, service);

    application.start();
  }

  private static Application getApplication(Jdbi jdbi, Service service) {
    TransactionManager transactionManager = new JdbiTransactionManager(jdbi);

    var userService = new UserServiceImplementation(new UserRepositoryImplementation(jdbi));
    var roomService = new RoomServiceImplementation(new RoomRepositoryImplementation(jdbi));
    var reservationService =
        new ReservationServiceImplementation(
            new ReservationRepositoryImplementation(jdbi),
            new RoomRepositoryImplementation(jdbi),
            transactionManager);

    ObjectMapper objectMapper = new ObjectMapper();

    return new Application(
        List.of(
            new RoomController(service, objectMapper, roomService),
            new UserController(service, objectMapper, userService),
            new ReservationController(service, objectMapper, reservationService),
            new ReservationTemplateController(
                service, reservationService, TemplateFactory.freeMarkerEngine())));
  }
}
