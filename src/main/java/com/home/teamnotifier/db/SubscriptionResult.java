package com.home.teamnotifier.db;

import com.home.teamnotifier.core.BroadcastInformation;
import com.home.teamnotifier.core.responses.notification.Subscription;
import com.home.teamnotifier.core.responses.status.ServerInfo;

public class SubscriptionResult {
    private final BroadcastInformation<Subscription> messageToOthers;
    private final ServerInfo messageToActor;

    SubscriptionResult(final BroadcastInformation<Subscription> messageToOthers,
                       final ServerInfo messageToActor) {
        this.messageToOthers = messageToOthers;
        this.messageToActor = messageToActor;
    }

    public BroadcastInformation<Subscription> getMessageToOthers() {
        return messageToOthers;
    }

    public ServerInfo getMessageToActor() {
        return messageToActor;
    }
}
