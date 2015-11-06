package com.home.teamnotifier.gateways;

import com.home.teamnotifier.resource.auth.UserInfo;
import java.util.List;

public interface SubscriptionGateway {
  List<String> subscribe(final String userName, final int serverId);

  List<String> unsubscribe(final String userName, final int serverId);

  List<String> reserve(final String userName, final int applicationId)
  throws AlreadyReserved;

  List<String> free(final String userName, final int applicationId)
  throws NotReserved;
}
