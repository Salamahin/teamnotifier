package com.home.teamnotifier.authentication;

import com.home.teamnotifier.db.TransactionHelper;
import java.util.Optional;

public class DbUserGateway implements UserGateway {
  private final TransactionHelper transactionHelper = TransactionHelper.getInstance();

  @Override
  public User userById(final Integer userId) {
    final com.home.teamnotifier.db.User storedUser =
        transactionHelper.transaction(em -> em.find(com.home.teamnotifier.db.User.class, userId));
    if(storedUser == null)
    return null;

    return new User(storedUser.getName(), storedUser.getSurname());
  }

  @Override
  public Optional<User> userByLoginPassword(final String login, final String password) {
    return null;
  }
}
