package com.home.teamnotifier.health;

import com.codahale.metrics.health.HealthCheck;
import com.home.teamnotifier.core.AppServerAvailabilityChecker;

public class AppServerStates extends HealthCheck {
    private final AppServerAvailabilityChecker checker;

    public AppServerStates(final AppServerAvailabilityChecker checker) {
        this.checker = checker;
    }

    @Override
    protected Result check() throws Exception {
        final String report = checker.report();
        return  Result.healthy(report);
    }
}
