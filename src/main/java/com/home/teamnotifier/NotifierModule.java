package com.home.teamnotifier;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.home.teamnotifier.gateways.HardcodedUserGateway;
import com.home.teamnotifier.gateways.UserGateway;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Singleton;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class NotifierModule extends AbstractModule {
  private static final Logger LOGGER = LoggerFactory.getLogger(NotifierModule.class);

  @Override
  protected void configure() {
    bind(UserGateway.class).to(HardcodedUserGateway.class);
  }

  @Provides
  @Singleton
  Executor newExecutor(final Config config) {
    final Config systemConfig = config.getConfig("system").resolve();
    final int poolSize = systemConfig.getInt("pool.size");
    return Executors.newFixedThreadPool(poolSize, new ThreadFactoryBuilder().setNameFormat
        ("cam-pool-%d").build());
  }

  @Provides
  @Singleton
  ScheduledExecutorService newScheduledExecutorService(final Config config) {
    final Config systemConfig = config.getConfig("system").resolve();
    final int poolSize = systemConfig.getInt("pool.size");
    return Executors.newScheduledThreadPool(poolSize,
        new ThreadFactoryBuilder().setNameFormat("websocket-pool-%d").build());
  }

  @Provides
  @Singleton
  Config config() {
    try {
      return ConfigFactory
          .parseResources(Thread.currentThread().getContextClassLoader(), "application.conf")
          .resolve();
    } catch (Exception e) {
      LOGGER.error("Config 'application.conf' not found. Cannot proceed");
      throw new IllegalStateException(e);
    }
  }

  //  @Provides
  //  @Singleton
  //  Set<Environment> environments(final Config config) {
  //    final ImmutableSet.Builder<Environment> builder = ImmutableSet.builder();
  //
  //    config.getConfigList("environment").stream()
  //        .map(c -> new Environment(c.getString("name"), readServersConfigForEnvironment(c)))
  //        .forEach(builder::add);
  //
  //    return builder.build();
  //  }
  //
  //  private Set<AppServer> readServersConfigForEnvironment(final Config environmentConfig) {
  //    return environmentConfig.getConfigList("appServer").stream()
  //        .map(c -> new AppServer(c.getString("name"), readApplicationsForAppServer(c)))
  //        .collect(toSet());
  //  }
  //
  //  private Set<SharedApplication> readApplicationsForAppServer(final Config appServerConfig) {
  //    return appServerConfig.getConfigList("application").stream()
  //        .map(c -> new SharedApplication(c.getString("name")))
  //        .collect(toSet());
  //  }
}
