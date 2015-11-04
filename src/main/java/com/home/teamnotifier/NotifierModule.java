package com.home.teamnotifier;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.*;
import com.home.teamnotifier.db.TransactionHelper;
import com.home.teamnotifier.gateways.*;
import javax.inject.Singleton;
import java.util.concurrent.*;

public class NotifierModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(UserGateway.class).to(DbUserGateway.class).in(Singleton.class);
    bind(EnvironmentGateway.class).to(DbEnvironmentGateway.class).in(Singleton.class);
    bind(TransactionHelper.class).in(Singleton.class);
  }

  @Provides
  @Singleton
  public Executor newExecutor(
      final NotifierConfiguration configuration
  ) {
    return Executors.newFixedThreadPool(
        configuration.getExecutorsConfiguration().getPoolSize(),
        new ThreadFactoryBuilder().setNameFormat("cam-pool-%d").build()
    );
  }

  @Provides
  @Singleton
  public ScheduledExecutorService newScheduledExecutorService(
      final NotifierConfiguration configuration
  ) {
    return Executors.newScheduledThreadPool(
        configuration.getExecutorsConfiguration().getPoolSize(),
        new ThreadFactoryBuilder().setNameFormat("websocket-pool-%d").build()
    );
  }
}
