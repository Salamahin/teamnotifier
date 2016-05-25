package com.home.teamnotifier.db;

import com.home.teamnotifier.core.BroadcastInformation;
import com.home.teamnotifier.core.responses.notification.Subscription;
import com.home.teamnotifier.core.responses.action.ServerSubscribersInfo;

public class SubscriptionResult {
    private final BroadcastInformation<Subscription> messageToOthers;
    private final ServerSubscribersInfo messageToActor;

    SubscriptionResult(final BroadcastInformation<Subscription> messageToOthers,
                       final ServerSubscribersInfo messageToActor) {
        this.messageToOthers = messageToOthers;
        this.messageToActor = messageToActor;
    }

    public BroadcastInformation<Subscription> getMessageToOthers() {
        return messageToOthers;
    }

    public ServerSubscribersInfo getMessageToActor() {
        return messageToActor;
    }
}
