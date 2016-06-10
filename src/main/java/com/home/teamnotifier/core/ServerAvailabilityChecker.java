package com.home.teamnotifier.core;

import com.home.teamnotifier.db.ServerEntity;

import java.util.Map;

public interface ServerAvailabilityChecker {
    Map<ServerEntity, Boolean> getAvailability();

    String report();

    void start();

    void stop();
}
