package org.example.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.http.HttpStatus;
import org.example.controller.Controller;
import org.example.user.UserExceptions;
import org.example.user.UserService;
import org.example.user.controller.dto.UserErrorResponse;
import org.example.user.controller.dto.UserRequest;
import org.example.user.controller.dto.UserResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

public class UserController implements Controller {
  private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
  private final Service service;
  private final ObjectMapper objectMapper;
  private final UserService userService;

  public UserController(Service service, ObjectMapper objectMapper, UserService userService) {
    this.service = service;
    this.objectMapper = objectMapper;
    this.userService = userService;
  }

  @Override
  public void initializeEndpoints() {
    findUserById();
    createUser();
  }

  private void findUserById() {
    service.get(
        "/api/user/:id",
        (Request request, Response response) -> {
          response.type("application/json");
          String id = request.params("id");

          try {
            var userId = Long.parseLong(id);

            var user = userService.findUserById(userId);
            response.status(HttpStatus.OK_200);
            LOG.debug("User successfully added");
            return objectMapper.writeValueAsString(
                new UserResponse.FindUser(user.id(), user.name()));
          } catch (UserExceptions.UserNotFoundException e) {
            LOG.warn("Cannot create a new user", e);
            response.status(HttpStatus.NOT_FOUND_404);
            return objectMapper.writeValueAsString(new UserErrorResponse(e.getMessage()));
          } catch (RuntimeException e) {
            LOG.error("Unhandled error", e);
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            return objectMapper.writeValueAsString(new UserErrorResponse("Internal server error"));
          }
        });
  }

  private void createUser() {
    service.post(
        "/api/user",
        (Request request, Response response) -> {
          response.type("application/json");

          String body = request.body();

          UserRequest.CreateUser addUserRequest =
              objectMapper.readValue(body, UserRequest.CreateUser.class);

          try {
            var userId = userService.createUser(addUserRequest.name());

            response.status(HttpStatus.CREATED_201);
            LOG.debug("User successfully added");
            return objectMapper.writeValueAsString(new UserResponse.CreateUser(userId));
          } catch (UserExceptions.UserNotFoundException e) {
            LOG.warn("Cannot create a new user", e);
            response.status(HttpStatus.BAD_REQUEST_400);
            return objectMapper.writeValueAsString(new UserErrorResponse(e.getMessage()));
          } catch (RuntimeException e) {
            LOG.error("Unhandled error", e);
            response.status(HttpStatus.INTERNAL_SERVER_ERROR_500);
            return objectMapper.writeValueAsString(new UserErrorResponse("Internal server error"));
          }
        });
  }

  // TODO: 12/9/23 updateUser method

  // TODO: 12/9/23 deleteUser method
}
