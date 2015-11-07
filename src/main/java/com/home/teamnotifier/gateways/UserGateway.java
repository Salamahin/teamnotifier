package com.home.teamnotifier.gateways;

public interface UserGateway {
  UserCredentials userCredentials(final String userName);
  void newUser(final String userName, final String password);
}
