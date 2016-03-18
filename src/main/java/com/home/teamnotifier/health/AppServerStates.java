package com.home.teamnotifier.health;

import com.codahale.metrics.health.HealthCheck;
import com.google.inject.Inject;
import com.home.teamnotifier.core.ServerAvailabilityChecker;

public class AppServerStates extends HealthCheck {
    private final ServerAvailabilityChecker checker;

    @Inject
    public AppServerStates(final ServerAvailabilityChecker checker) {
        this.checker = checker;
    }

    @Override
    protected Result check() throws Exception {
        final String report = checker.report();
        return  Result.healthy(report);
    }
}
