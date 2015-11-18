package com.home.teamnotifier.core;

import com.google.common.collect.Range;
import com.google.inject.Inject;
import com.home.teamnotifier.core.responses.*;
import com.home.teamnotifier.gateways.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class ResourceMonitor {

  private static final Logger LOGGER = LoggerFactory.getLogger(ResourceMonitor.class);

  private final EnvironmentGateway environmentGateway;
  private final SubscriptionGateway subscriptionGateway;
  private final SharedResourceActionsGateway sharedResourceActionsGateway;
  private final NotificationManager notificationManager;

  @Inject
  public ResourceMonitor(
      final EnvironmentGateway environmentGateway,
      final SubscriptionGateway subscriptionGateway,
      final SharedResourceActionsGateway sharedResourceActionsGateway,
      final NotificationManager notificationManager
  ) {

    this.environmentGateway = environmentGateway;
    this.subscriptionGateway = subscriptionGateway;
    this.sharedResourceActionsGateway = sharedResourceActionsGateway;
    this.notificationManager = notificationManager;
  }

  public void reserve(final String userName, final int applicationId) {
    LOGGER.info("User {} reserves resource id {}", userName, applicationId);
    final BroadcastInformation information = subscriptionGateway.reserve(userName, applicationId);
    fireNotification(information);
  }

  void fireNotification(final BroadcastInformation information) {
    notificationManager.pushToClients(information.getSubscribers(), information.getStringToPush());
  }

  public void subscribe(final String userName, final int serverId) {
    LOGGER.info("User {} subscribed on server id {}", userName, serverId);
    final BroadcastInformation information = subscriptionGateway.subscribe(userName, serverId);
    fireNotification(information);
  }

  public void unsubscribe(final String userName, final int serverId) {
    LOGGER.info("User {} unsubscribed from server id {}", userName, serverId);
    final BroadcastInformation information = subscriptionGateway.unsubscribe(userName, serverId);
    fireNotification(information);
  }

  public void free(final String userName, final int applicationId) {
    LOGGER.info("User {} free resource id {}", userName, applicationId);
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
    LOGGER.info("User {} made new action on resource id {}: {}", userName, applicationId, desc);
    final BroadcastInformation information = sharedResourceActionsGateway
        .newAction(userName, applicationId, desc);
    fireNotification(information);
  }
}
