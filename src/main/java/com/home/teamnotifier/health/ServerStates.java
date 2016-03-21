package com.home.teamnotifier.health;

import com.codahale.metrics.health.HealthCheck;
import com.google.inject.Inject;
import com.home.teamnotifier.core.ServerAvailabilityChecker;

public class ServerStates extends HealthCheck {
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
}
