package com.home.teamnotifier.health;

import com.codahale.metrics.health.HealthCheck;
import com.google.inject.Inject;
import com.home.teamnotifier.core.ServerAvailabilityChecker;
import ru.vyarus.dropwizard.guice.module.installer.feature.health.NamedHealthCheck;

public class ServerStates extends NamedHealthCheck {
    private final ServerAvailabilityChecker checker;

    @Inject
    public ServerStates(final ServerAvailabilityChecker checker) {
        this.checker = checker;
    }

    @Override
    protected Result check() throws Exception {
        final String report = checker.report();
        return  Result.healthy(report);
    }

    @Override
    public String getName() {
        return "Servers states";
    }
}
