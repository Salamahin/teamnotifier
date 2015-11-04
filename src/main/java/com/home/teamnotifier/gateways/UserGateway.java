package com.home.teamnotifier.gateways;

import com.home.teamnotifier.authentication.User;
import java.util.Optional;

public interface UserGateway {
  User userById(Integer userId);

  Optional<User> userByLoginPassword(String login, String password);
}
