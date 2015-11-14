package com.home.teamnotifier.db;

import com.google.inject.Inject;
import com.home.teamnotifier.gateways.*;
import com.home.teamnotifier.utils.PasswordHasher;
import org.slf4j.*;
import static com.home.teamnotifier.db.DbGatewayCommons.getUserEntity;

public class DbUserGateway implements UserGateway {
  private static final Logger LOGGER = LoggerFactory.getLogger(DbUserGateway.class);

  private final TransactionHelper transactionHelper;

  @Inject
  public DbUserGateway(final TransactionHelper transactionHelper) {
    this.transactionHelper = transactionHelper;
  }

  @Override
  public UserCredentials userCredentials(final int id) {
    try {
      final UserEntity entity = transactionHelper.transaction(em -> em.find(UserEntity.class, id));
      return new UserCredentials(entity.getId(), entity.getName(), entity.getPassHash());
    } catch (Exception exc) {
      LOGGER.error("Failed to get user by name", exc);
    }

    return null;
  }

  @Override
  public UserCredentials userCredentials(final String userName) {
    final UserEntity entity = getEntityByName(userName);
    if (entity != null) {
      return new UserCredentials(entity.getId(), entity.getName(), entity.getPassHash());
    } else {
      return null;
    }
  }

  @Override
  public void newUser(final String userName, final String password) {
    LOGGER.info("New user {} creation", userName);
    final UserEntity entity = new UserEntity(userName, PasswordHasher.toMd5Hash(password));
    transactionHelper.transaction(em -> em.merge(entity));
  }

  private UserEntity getEntityByName(String name) {
    try {
      return transactionHelper.transaction(em -> getUserEntity(name, em));
    } catch (Exception exc) {
      LOGGER.error("Failed to get user by name", exc);
    }

    return null;
  }
}
