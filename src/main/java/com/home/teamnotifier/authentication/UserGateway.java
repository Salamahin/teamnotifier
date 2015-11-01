package com.home.teamnotifier.authentication;

import java.util.Optional;

public interface UserGateway {
  User userById(Integer userId);

  Optional<User> userByLoginPassword(String login, String password);
}
