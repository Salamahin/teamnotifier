package com.home.teamnotifier.db;

import com.home.teamnotifier.utils.PasswordHasher;
import java.util.UUID;

final public class Commons {
  private Commons() {
    throw new AssertionError();
  }

  static final TransactionHelper HELPER = new TransactionHelper();

  public static UserEntity createPersistedUser(
      final String userName,
      final String pass
  ) {
    final UserEntity entity = new UserEntity(userName, PasswordHasher.toMd5Hash(pass));
    return HELPER.transaction(em -> em.merge(entity));
  }

  static String getRandomString() {
    return UUID.randomUUID().toString();
  }


  public static EnvironmentEntity createPersistedEnvironmentWithOneServerAndOneResource(
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
