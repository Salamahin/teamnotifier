package com.home.teamnotifier.gateways;

import com.home.teamnotifier.db.*;
import java.util.UUID;

final class Commons {
  private Commons() {
    throw new AssertionError();
  }

  static final TransactionHelper HELPER = new TransactionHelper();

  static UserEntity createPersistedUserWithRandomPassHash(final String userName) {
    final UserEntity entity = new UserEntity(userName, getRandomString());
    return HELPER.transaction(em -> em.merge(entity));
  }

  static String getRandomString() {
    return UUID.randomUUID().toString();
  }


  static EnvironmentEntity createPersistedEnvironmentWithOneServerAndOneResource(
      final String envName,
      final String serverName,
      final String appName
  ) {
    final EnvironmentEntity entity = new EnvironmentEntity(envName);
    final AppServerEntity appServerEntity=entity.newAppServer(serverName);
    appServerEntity.newSharedResource(appName);

    return HELPER.transaction(em -> em.merge(entity));
  }
}
