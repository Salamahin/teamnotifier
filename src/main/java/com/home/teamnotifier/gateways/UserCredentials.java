package com.home.teamnotifier.gateways;

public class UserCredentials
{
  private final String userName;
  private final String passHash;

  public UserCredentials(String userName, String passHash)
  {
    this.userName=userName;
    this.passHash=passHash;
  }

  public String getUserName()
  {
    return userName;
  }

  public String getPassHash()
  {
    return passHash;
  }
}
