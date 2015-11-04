package com.home.teamnotifier.gateways;

import com.home.teamnotifier.authentication.User;
import java.util.Optional;

public interface UserGateway {
  User userById(int userId);
  String getPasswordHash(String userName);
}
