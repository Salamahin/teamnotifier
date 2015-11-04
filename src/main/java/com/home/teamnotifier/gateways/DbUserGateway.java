package com.home.teamnotifier.gateways;

import com.google.common.base.Preconditions;
import com.home.teamnotifier.authentication.User;
import com.home.teamnotifier.db.*;
import java.util.Optional;

public class DbUserGateway implements UserGateway {
  private final TransactionHelper transactionHelper = TransactionHelper.getInstance();

  @Override
  public User userById(final Integer userId) {
    final UserEntity storedUserEntity =
        transactionHelper.transaction(em -> em.find(UserEntity.class, userId));
    Preconditions.checkNotNull(storedUserEntity, "User with id %s not found", userId);
    return new User(storedUserEntity.getId(), storedUserEntity.getName());
  }

  @Override
  public Optional<User> userByLoginPassword(final String login, final String password) {
    throw new IllegalStateException("Not implemented");
  }
}
