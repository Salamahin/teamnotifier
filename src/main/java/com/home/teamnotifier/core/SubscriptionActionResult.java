package com.home.teamnotifier.core;

import com.home.teamnotifier.core.responses.action.ServerSubscribersInfo;
import com.home.teamnotifier.core.responses.notification.Subscription;

public class SubscriptionActionResult {
    private final BroadcastInformation<Subscription> broadcastInformation;
    private final ServerSubscribersInfo subscribersInfo;

    public SubscriptionActionResult(
            final BroadcastInformation<Subscription> broadcastInformation,
            final ServerSubscribersInfo subscribersInfo
    ) {
        this.broadcastInformation = broadcastInformation;
        this.subscribersInfo = subscribersInfo;
    }

    public BroadcastInformation<Subscription> getBroadcastInformation() {
        return broadcastInformation;
    }

    public ServerSubscribersInfo getSubscribersInfo() {
        return subscribersInfo;
    }
}
