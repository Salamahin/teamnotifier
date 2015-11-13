package com.home.teamnotifier;

import com.home.teamnotifier.db.AppServerEntity;
import com.home.teamnotifier.db.EnvironmentEntity;
import com.home.teamnotifier.db.TransactionHelper;
import com.home.teamnotifier.db.UserEntity;
import com.home.teamnotifier.utils.PasswordHasher;
import java.util.UUID;

public final class TestHelper {

  public final TransactionHelper TRANSACTION_HELPER;

  public TestHelper(final TransactionHelper helper) {
    this.TRANSACTION_HELPER = helper;
  }

  public TestHelper() {
    TRANSACTION_HELPER = new TransactionHelper();
  }

  public UserEntity createPersistedUser(
      final String userName,
      final String pass
  ) {
    final UserEntity entity = new UserEntity(userName, PasswordHasher.toMd5Hash(pass));
    return TRANSACTION_HELPER.transaction(em -> em.merge(entity));
  }

  public static String getRandomString() {
    return UUID.randomUUID().toString();
  }


  public EnvironmentEntity createPersistedEnvironmentWithOneServerAndOneResource(
      final String envName,
      final String serverName,
      final String appName
  ) {
    final EnvironmentEntity entity = new EnvironmentEntity(envName);
    final AppServerEntity appServerEntity=entity.newAppServer(serverName);
    appServerEntity.newSharedResource(appName);

    return TRANSACTION_HELPER.transaction(em -> em.merge(entity));
  }
}