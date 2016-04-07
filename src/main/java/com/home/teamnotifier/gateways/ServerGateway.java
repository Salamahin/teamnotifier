package com.home.teamnotifier.gateways;

import com.home.teamnotifier.db.ServerEntity;

import java.util.Set;

public interface ServerGateway {
    Set<ServerEntity> getImmutableSetOfObservableServers();
}
