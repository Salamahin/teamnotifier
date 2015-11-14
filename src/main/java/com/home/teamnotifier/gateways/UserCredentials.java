package com.home.teamnotifier.gateways;

public class UserCredentials {
  private final String userName;

  private final int id;

  private final String passHash;

  public UserCredentials(final int id, final String userName, final String passHash) {
    this.userName = userName;
    this.passHash = passHash;
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public String getUserName() {
    return userName;
  }

  public String getPassHash() {
    return passHash;
  }
}
