package com.home.teamnotifier.authentication;

import java.security.Principal;

public class User implements Principal {
  private final String name;
  private final int id;

  public User(final int id, final String name) {
    this.id = id;
    this.name = name;
  }

  public int getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }
}
