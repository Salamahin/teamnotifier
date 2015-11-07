package com.home.teamnotifier.health;

import com.codahale.metrics.health.HealthCheck;
import com.home.teamnotifier.gateways.EnvironmentGateway;

public class DbConnection extends HealthCheck {
  private final EnvironmentGateway gateway;

  public DbConnection(final EnvironmentGateway gateway) {
    this.gateway = gateway;
  }

  @Override
  protected Result check()
  throws Exception {
    try {
      gateway.status();
      return Result.healthy();
    } catch (Exception exc) {
      return Result.unhealthy(exc);
    }
  }
}
