package com.home.teamnotifier.gateways;

import com.home.teamnotifier.db.*;
import java.util.UUID;

final class Commons {
  private Commons() {
    throw new AssertionError();
  }

  static final TransactionHelper HELPER = new TransactionHelper();

  static UserEntity createPersistedUserWithRandomPassHash(final String userName) {
    final UserEntity entity = new UserEntity();
    entity.setName(userName);
    entity.setPassHash(getRandomString());
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
    final EnvironmentEntity entity = new EnvironmentEntity();
    entity.setName(envName);
    entity.getAppServers().add(createServerWithOneApplication(entity, serverName, appName));
    return HELPER.transaction(em -> em.merge(entity));
  }

  private static AppServerEntity createServerWithOneApplication(
      final EnvironmentEntity environmentEntity,
      final String serverName,
      final String resourceName
  ) {
    final AppServerEntity entity = new AppServerEntity();
    entity.setName(serverName);
    entity.setEnvironment(environmentEntity);
    entity.getResources().add(createSharedResource(entity, resourceName));
    return entity;
  }

  private static SharedResourceEntity createSharedResource(
      final AppServerEntity appServerEntity,
      final String appName
  ) {
    final SharedResourceEntity entity = new SharedResourceEntity();
    entity.setAppServer(appServerEntity);
    entity.setName(appName);
    return entity;
  }
}
