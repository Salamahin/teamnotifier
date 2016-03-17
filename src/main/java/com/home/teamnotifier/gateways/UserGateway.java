package com.home.teamnotifier.gateways;

import com.home.teamnotifier.db.UserEntity;
import com.home.teamnotifier.gateways.exceptions.NoSuchUser;

public interface UserGateway {
    UserEntity get(final int id) throws NoSuchUser;

    UserEntity get(final String userName) throws NoSuchUser;

    void newUser(final String userName, final String password);
}
