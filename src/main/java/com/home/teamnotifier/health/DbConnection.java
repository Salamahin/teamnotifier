package com.home.teamnotifier.health;

import com.google.inject.Inject;
import com.home.teamnotifier.gateways.EnvironmentGateway;
import ru.vyarus.dropwizard.guice.module.installer.feature.health.NamedHealthCheck;

public class DbConnection extends NamedHealthCheck {
    private final EnvironmentGateway gateway;

    @Inject
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

    @Override
    public String getName() {
        return "Database connection";
    }
}
