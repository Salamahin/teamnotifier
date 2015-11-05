package com.home.teamnotifier.gateways;

import com.home.teamnotifier.resource.auth.UserInfo;

import java.util.List;

public interface SubscriptionGateway {
  List<UserInfo> getSubscribers(final int serverId);
  void subscribe(final String userName, final int serverId);
  void unsubscribe(final String userName, final int serverId);
  void reserve(final String userName, final int applicationId) throws AlreadyReserved;
  void free(final String userName, final int applicationId) throws NotReserved;
}
