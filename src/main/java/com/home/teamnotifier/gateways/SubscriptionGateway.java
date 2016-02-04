package com.home.teamnotifier.gateways;

public interface SubscriptionGateway {
    BroadcastInformation subscribe(final String userName, final int serverId)
            throws NoSuchServer, NoSuchUser, AlreadySubscribed;

    BroadcastInformation unsubscribe(final String userName, final int serverId)
            throws NoSuchServer, NoSuchUser, NotSubscribed;

    BroadcastInformation reserve(final String userName, final int applicationId)
            throws AlreadyReserved, NoSuchResource, NoSuchUser;

    BroadcastInformation free(final String userName, final int applicationId)
            throws NotReserved, NoSuchResource, NoSuchUser, ReservedByDifferentUser;
}
