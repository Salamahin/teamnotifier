package com.home.teamnotifier.gateways;

public interface SubscriptionGateway {
  void subscribe(final String userName, final int serverId);
  void unsubscribe(final String userName, final int serverId);
  void reserve(final String userName, final int applicationId);
  void free(final String userName, final int applicationId);
}
