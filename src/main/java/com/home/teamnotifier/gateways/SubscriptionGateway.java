package com.home.teamnotifier.gateways;

public interface SubscriptionGateway {
  void subscribe(final int userId, final int serverId);
  void unsubscribe(final int userId, final int serverId);
  void reserve(final int userId, final int applicationId);
  void free(final int userId, final int applicationId);
}
