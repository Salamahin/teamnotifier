package com.home.teamnotifier.gateways;

public interface UserGateway {
    UserCredentials userCredentials(final int id) throws NoSuchUser;

    UserCredentials userCredentials(final String userName) throws NoSuchUser;

    void newUser(final String userName, final String password);
}
