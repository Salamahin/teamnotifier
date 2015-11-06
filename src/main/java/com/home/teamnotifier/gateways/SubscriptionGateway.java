package com.home.teamnotifier.gateways;

public interface SubscriptionGateway {
  BroadcastInformation subscribe(final String userName, final int serverId);

  BroadcastInformation unsubscribe(final String userName, final int serverId);

  BroadcastInformation reserve(final String userName, final int applicationId)
  throws AlreadyReserved;

  BroadcastInformation free(final String userName, final int applicationId)
  throws NotReserved;
}
