package com.home.teamnotifier.gateways;

import com.home.teamnotifier.authentication.User;
import java.util.Optional;

public class HardcodedUserGateway implements UserGateway {
  @Override
  public Optional<User> userByLoginPassword(final String login, final String password) {
    return Optional.of(userById(12412));
  }

  @Override
  public User userById(final Integer userId) {
    return new User(userId, "hardcodedSurname");
  }
}
