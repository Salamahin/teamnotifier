package com.home.teamnotifier.authentication;

import java.security.Principal;

public class User implements Principal {
  private final String userName;

  private final String userSurname;

  public User(final String userName, final String userSurname) {
    this.userName = userName;
    this.userSurname = userSurname;
  }

  @Override
  public String getName() {
    return userName;
  }
}
