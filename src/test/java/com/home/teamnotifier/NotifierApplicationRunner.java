package com.home.teamnotifier;

import com.google.common.io.Resources;
import com.google.inject.Injector;
import com.home.teamnotifier.db.AppServerEntity;
import com.home.teamnotifier.db.EnvironmentEntity;
import com.home.teamnotifier.db.TransactionHelper;
import com.home.teamnotifier.db.UserEntity;
import com.home.teamnotifier.utils.PasswordHasher;

public class NotifierApplicationRunner
{
  public static void main(String[] args)
      throws Exception
  {
    final String yamlPath=Resources.getResource("web.yml").getFile();
    final NotifierApplication application=new NotifierApplication();
    application.run("server", yamlPath);

    final Injector injector=Injection.INJECTION_BUNDLE.getInjector();
    final TransactionHelper helper=injector.getInstance(TransactionHelper.class);

    createEnvironmentWithOneSeverAndOneApplication(
        helper,
        "env",
        "srv",
        "app"
    );

    createUser(helper, "hello", "world");
  }

  private static void createUser(
      final TransactionHelper helper,
      final String name,
      final String pass
  ) {
    helper.transaction(em -> {
          final UserEntity user=new UserEntity(name, PasswordHasher.toMd5Hash(pass));

          em.merge(user);

          return null;
        }
    );
  }

  private static void createEnvironmentWithOneSeverAndOneApplication(
      final TransactionHelper helper,
      final String envName,
      final String srvName,
      final String appName
  )
  {
    helper.transaction(em -> {
      final EnvironmentEntity environment=new EnvironmentEntity(envName);
      final AppServerEntity server=environment.newAppServer(srvName);
      server.newSharedResource(appName);

      em.merge(environment);

      return null;
    });
  }
}