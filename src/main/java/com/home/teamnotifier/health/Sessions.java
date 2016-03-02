package com.home.teamnotifier.health;

import com.codahale.metrics.health.HealthCheck;
import com.google.inject.Inject;
import com.home.teamnotifier.web.socket.ClientManager;

public class Sessions extends HealthCheck {

    private final ClientManager manager;

    @Inject
    public Sessions(final ClientManager manager) {
        this.manager = manager;
    }

    @Override
    protected Result check() throws Exception {
        return Result.healthy(manager.getClientNamesList().toString());
    }
}
