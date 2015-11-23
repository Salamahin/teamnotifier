package com.home.teamnotifier.health;

import com.codahale.metrics.health.HealthCheck;
import com.home.teamnotifier.web.socket.ClientManager;

public class Sessions extends HealthCheck {

    private final ClientManager manager;

    public Sessions(ClientManager manager) {
        this.manager = manager;
    }

    @Override
    protected Result check() throws Exception {
        return Result.healthy(manager.getClientNamesList().toString());
    }
}
