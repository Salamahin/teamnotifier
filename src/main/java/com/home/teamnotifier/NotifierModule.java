package com.home.teamnotifier;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.*;
import com.home.teamnotifier.core.NotificationManager;
import com.home.teamnotifier.db.*;
import com.home.teamnotifier.gateways.*;
import com.home.teamnotifier.web.socket.ClientManager;

import javax.inject.Singleton;
import java.util.concurrent.*;

final class NotifierModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(UserGateway.class)
        .to(DbUserGateway.class)
        .in(Singleton.class);

    bind(EnvironmentGateway.class)
        .to(DbEnvironmentGateway.class)
        .in(Singleton.class);

    bind(SharedResourceActionsGateway.class)
        .to(DbSharedResourceActionsGateway.class)
        .in(Singleton.class);

    bind(SubscriptionGateway.class)
        .to(DbSubscriptionGateway.class)
        .in(Singleton.class);

    bind(TransactionHelper.class)
        .in(Singleton.class);

    bind(NotificationManager.class)
        .to(ClientManager.class)
        .in(Singleton.class);
  }

  @Provides
  @Singleton
  public Executor newExecutor(final NotifierConfiguration configuration) {
    return Executors.newFixedThreadPool(
        configuration.getExecutorsConfiguration().getPoolSize(),
        new ThreadFactoryBuilder().setNameFormat("websocket-pool-%d").build()
    );
  }
}
