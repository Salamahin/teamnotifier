package com.home.teamnotifier.gateways;

import com.google.common.collect.ImmutableList;
import com.home.teamnotifier.db.AppServerEntity;

public interface AppServerGateway {
    ImmutableList<AppServerEntity> getObservableServers();
}
