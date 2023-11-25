package org.example.user;

import java.util.ArrayList;

public class User {
  public long id;
  public String name;

  public ArrayList<InformationAboutReservationUser> informationList = new ArrayList<>();

  public User(String name) {
    this.id = UserActions.generateID();
    this.name = name;
    this.informationList = informationList;
  }
}
