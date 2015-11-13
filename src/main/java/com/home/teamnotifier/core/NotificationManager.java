package com.home.teamnotifier.core;

import java.util.Collection;

public interface NotificationManager {
    void pushToClients(Collection<String> userNames, String message);
}
