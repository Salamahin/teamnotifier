package com.home.teamnotifier.health;

import com.google.inject.Inject;
import com.home.teamnotifier.web.socket.ClientManager;
import ru.vyarus.dropwizard.guice.module.installer.feature.health.NamedHealthCheck;

public class Sessions extends NamedHealthCheck {

    private final ClientManager manager;

    @Inject
    public Sessions(final ClientManager manager) {
        this.manager = manager;
    }

    @Override
    protected Result check() throws Exception {
        return Result.healthy(manager.getClientNamesList().toString());
    }

    @Override
    public String getName() {
        return "Opened user sessions";
    }
}
