package com.home.teamnotifier;

import com.google.common.io.Resources;
import com.google.inject.Injector;
import com.home.teamnotifier.db.TransactionHelper;

public class NotifierApplicationRunner {
  public static void main(String[] args)
  throws Exception {
    final String yamlPath = Resources.getResource("web.yml").getFile();
    final NotifierApplication application = new NotifierApplication();
    application.run("server", yamlPath);

    final Injector injector = Injection.INJECTION_BUNDLE.getInjector();
    final DbPreparer helper = new DbPreparer(injector.getInstance(TransactionHelper.class));
    helper.createPersistedEnvironmentWithOneServerAndOneResource("env1", "srv1", "app1");
    helper.createPersistedEnvironmentWithOneServerAndOneResource("env2", "srv2", "app2");
    helper.createPersistedUser("user", "pass");
  }
}