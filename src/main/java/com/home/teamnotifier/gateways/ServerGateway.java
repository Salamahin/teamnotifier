package com.home.teamnotifier.gateways;

import com.home.teamnotifier.db.AppServerEntity;

import java.util.Set;

public interface ServerGateway {
    Set<AppServerEntity> getImmutableSetOfObservableServers();
}
