package org.example.user;

import java.util.concurrent.atomic.AtomicLong;

public class User {
  public AtomicLong id;
  public String name;

  public User(String name) {
    this.id = UserActions.generateID();
    this.name = name;
  }
}
