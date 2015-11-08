package com.home.teamnotifier.core;

import com.google.common.collect.Range;
import com.google.inject.Inject;
import com.home.teamnotifier.core.environment.*;
import com.home.teamnotifier.gateways.*;
import com.home.teamnotifier.web.socket.ClientManager;
import java.time.LocalDateTime;

public class ResourceMonitor {

  private final EnvironmentGateway environmentGateway;

  private final SubscriptionGateway subscriptionGateway;

  private final SharedResourceActionsGateway sharedResourceActionsGateway;

  private final ClientManager clientManager;

  @Inject
  public ResourceMonitor(
      final EnvironmentGateway environmentGateway,
      final SubscriptionGateway subscriptionGateway,
      final SharedResourceActionsGateway sharedResourceActionsGateway,
      final ClientManager clientManager
  ) {

    this.environmentGateway = environmentGateway;
    this.subscriptionGateway = subscriptionGateway;
    this.sharedResourceActionsGateway = sharedResourceActionsGateway;
    this.clientManager = clientManager;
  }

  public void reserve(final String userName, final int applicationId) {
    final BroadcastInformation information = subscriptionGateway.reserve(userName, applicationId);
    fireNotification(information);
  }

  void fireNotification(final BroadcastInformation information) {
    clientManager.pushToClients(information.getSubscribers(), information.getStringToPush());
  }

  public void subscribe(final String userName, final int serverId) {
    final BroadcastInformation information = subscriptionGateway.subscribe(userName, serverId);
    fireNotification(information);
  }

  public void unsubscribe(final String userName, final int serverId) {
    final BroadcastInformation information = subscriptionGateway.unsubscribe(userName, serverId);
    fireNotification(information);
  }

  public void free(final String userName, final int applicationId) {
    final BroadcastInformation information = subscriptionGateway.free(userName, applicationId);
    fireNotification(information);
  }

  public EnvironmentsInfo status() {
    return environmentGateway.status();
  }

  public ActionsInfo actionsInfo(final int applicationId, final Range<LocalDateTime> range) {
    return sharedResourceActionsGateway.getActions(applicationId, range);
  }

  public void newAction(final String userName, final int applicationId, final String desc) {
    final BroadcastInformation information = sharedResourceActionsGateway
        .newAction(userName, applicationId, desc);
    fireNotification(information);
  }
}
