package com.home.teamnotifier.gateways;

import com.home.teamnotifier.core.BroadcastInformation;
import com.home.teamnotifier.core.responses.notification.Reservation;
import com.home.teamnotifier.core.responses.notification.Subscription;
import com.home.teamnotifier.gateways.exceptions.*;

public interface SubscriptionGateway {
    BroadcastInformation<Subscription> subscribe(final String userName, final int serverId)
            throws NoSuchServer, NoSuchUser, AlreadySubscribed;

    BroadcastInformation<Subscription> unsubscribe(final String userName, final int serverId)
            throws NoSuchServer, NoSuchUser, NotSubscribed;

    BroadcastInformation<Reservation> reserve(final String userName, final int applicationId)
            throws AlreadyReserved, NoSuchResource, NoSuchUser;

    BroadcastInformation<Reservation> free(final String userName, final int applicationId)
            throws NotReserved, NoSuchResource, NoSuchUser, ReservedByDifferentUser;
}
