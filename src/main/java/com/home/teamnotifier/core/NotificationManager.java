package com.home.teamnotifier.core;

import com.home.teamnotifier.core.responses.notification.Notification;

import java.util.Collection;

public interface NotificationManager {
    void pushToClients(Collection<String> userNames, Notification message);
}
