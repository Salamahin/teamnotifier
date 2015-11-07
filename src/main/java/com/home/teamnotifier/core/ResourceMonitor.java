package com.home.teamnotifier.core;

import com.google.inject.Inject;
import com.home.teamnotifier.core.environment.EnvironmentsInfo;
import com.home.teamnotifier.gateways.*;
import com.home.teamnotifier.web.socket.ClientManager;

public class ResourceMonitor {

  private final EnvironmentGateway environmentGateway;

  private final SubscriptionGateway subscriptionGateway;

  private final ClientManager clientManager;

  @Inject
  public ResourceMonitor(
      final EnvironmentGateway environmentGateway,
      final SubscriptionGateway subscriptionGateway,
      final ClientManager clientManager
  ) {

    this.environmentGateway = environmentGateway;
    this.subscriptionGateway = subscriptionGateway;
    this.clientManager = clientManager;
  }

  public void reserve(final String userName, final int applicationId) {
    subscriptionGateway.reserve(userName, applicationId);
    fireNotification();
  }

  void fireNotification() {

  }

  public void subscribe(final String userName, final int serverId) {
    subscriptionGateway.subscribe(userName, serverId);
  }

  public void unsubscribe(final String userName, final int serverId) {
    subscriptionGateway.subscribe(userName, serverId);
  }

  public void free(final String userName, final int applicationId) {
    subscriptionGateway.free(userName, applicationId);
    fireNotification();
  }

  public EnvironmentsInfo status() {
    return environmentGateway.status();
  }
}
