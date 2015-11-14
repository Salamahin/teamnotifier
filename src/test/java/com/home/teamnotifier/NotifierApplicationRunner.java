package com.home.teamnotifier;

import com.google.common.io.Resources;
import com.google.inject.Injector;
import com.home.teamnotifier.db.AppServerEntity;
import com.home.teamnotifier.db.EnvironmentEntity;
import com.home.teamnotifier.db.TransactionHelper;
import com.home.teamnotifier.db.UserEntity;
import com.home.teamnotifier.utils.PasswordHasher;
import junit.framework.Test;

public class NotifierApplicationRunner {
  public static void main(String[] args)
  throws Exception {
    final String yamlPath = Resources.getResource("web.yml").getFile();
    final NotifierApplication application = new NotifierApplication();
    application.run("server", yamlPath);

    final Injector injector = Injection.INJECTION_BUNDLE.getInjector();
    final TestHelper helper = new TestHelper(injector.getInstance(TransactionHelper.class));
    helper.createPersistedEnvironmentWithOneServerAndOneResource("env", "srv", "app");
    helper.createPersistedUser("user", "pass");
  }
}