package com.home.teamnotifier.web.lifecycle;

import com.google.inject.Inject;
import com.home.teamnotifier.core.AppServerAvailabilityChecker;
import io.dropwizard.lifecycle.Managed;

public class ServerStatusCheckerManager implements Managed {
    public final AppServerAvailabilityChecker checker;

    @Inject
    public ServerStatusCheckerManager(AppServerAvailabilityChecker checker) {
        this.checker = checker;
    }

    @Override
    public void start() throws Exception {
        checker.start();
    }

    @Override
    public void stop() throws Exception {
        checker.stop();
    }
}
