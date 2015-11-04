package com.home.teamnotifier.authentication;

import com.google.common.base.Preconditions;
import com.home.teamnotifier.db.TransactionHelper;
import com.home.teamnotifier.db.UserEntity;
import java.util.Optional;

public class DbUserGateway implements UserGateway {
  private final TransactionHelper transactionHelper = TransactionHelper.getInstance();

  @Override
  public User userById(final Integer userId) {
    final UserEntity storedUserEntity =
        transactionHelper.transaction(em -> em.find(UserEntity.class, userId));
    Preconditions.checkNotNull(storedUserEntity, "User with id %s not found", userId);
    return new User(storedUserEntity.getName(), storedUserEntity.getSurname());
  }

  @Override
  public Optional<User> userByLoginPassword(final String login, final String password) {
    throw new IllegalStateException("Not implemented");
  }
}
