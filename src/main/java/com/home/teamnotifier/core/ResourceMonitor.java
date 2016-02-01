package com.home.teamnotifier.core;

import com.google.common.collect.Range;
import com.google.inject.Inject;
import com.home.teamnotifier.core.responses.action.ActionsInfo;
import com.home.teamnotifier.core.responses.status.EnvironmentsInfo;
import com.home.teamnotifier.gateways.BroadcastInformation;
import com.home.teamnotifier.gateways.EnvironmentGateway;
import com.home.teamnotifier.gateways.SharedResourceActionsGateway;
import com.home.teamnotifier.gateways.SubscriptionGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class ResourceMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceMonitor.class);

    private final EnvironmentGateway environmentGateway;
    private final SubscriptionGateway subscriptionGateway;
    private final SharedResourceActionsGateway sharedResourceActionsGateway;
    private final NotificationManager notificationManager;

    @Inject
    @SuppressWarnings("unused")
    private ResourceMonitor(
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
        final BroadcastInformation information = subscriptionGateway.reserve(userName, applicationId);
        fireNotification(information);
    }

    private void fireNotification(final BroadcastInformation information) {
        LOGGER.debug("New notification to be fired: {}", information);
        notificationManager.pushToClients(information.getSubscribers(), information.getValue());
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

    public ActionsInfo actionsInfo(final int applicationId, final Range<Instant> range) {
        return sharedResourceActionsGateway.getActions(applicationId, range);
    }

    public void newAction(final String userName, final int applicationId, final String desc) {
        final BroadcastInformation information =
                sharedResourceActionsGateway.newAction(userName, applicationId, desc);
        fireNotification(information);
    }
}
