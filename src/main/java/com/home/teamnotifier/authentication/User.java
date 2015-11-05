package com.home.teamnotifier.authentication;

import java.security.Principal;

public class User implements Principal {
  private final String name;

  public User(final String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }
}
