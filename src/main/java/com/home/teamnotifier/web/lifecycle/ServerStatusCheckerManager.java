package com.home.teamnotifier.web.lifecycle;

import com.google.inject.Inject;
import com.home.teamnotifier.core.ServerAvailabilityChecker;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerStatusCheckerManager implements Managed {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerStatusCheckerManager.class);

    private final ServerAvailabilityChecker checker;

    @Inject
    public ServerStatusCheckerManager(ServerAvailabilityChecker checker) {
        this.checker = checker;
    }

    @Override
    public void start() throws Exception {
        checker.start();
        LOGGER.info("Server availability checker launched");
    }

    @Override
    public void stop() throws Exception {
        checker.stop();
        LOGGER.info("Server availability checker stopped");
    }
}
