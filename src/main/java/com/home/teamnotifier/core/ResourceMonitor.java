package com.home.teamnotifier.core;

import com.google.common.collect.Range;
import com.google.inject.Inject;
import com.home.teamnotifier.core.responses.action.ResourceActionsHistory;
import com.home.teamnotifier.core.responses.action.ServerActionsHistory;
import com.home.teamnotifier.core.responses.status.EnvironmentsInfo;
import com.home.teamnotifier.gateways.EnvironmentGateway;
import com.home.teamnotifier.gateways.ActionsGateway;
import com.home.teamnotifier.gateways.ResourceDescription;
import com.home.teamnotifier.gateways.SubscriptionGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class ResourceMonitor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceMonitor.class);

    private final EnvironmentGateway environmentGateway;
    private final SubscriptionGateway subscriptionGateway;
    private final ActionsGateway actionsGateway;
    private final NotificationManager notificationManager;

    @Inject
    @SuppressWarnings("unused")
    private ResourceMonitor(
            final EnvironmentGateway environmentGateway,
            final SubscriptionGateway subscriptionGateway,
            final ActionsGateway actionsGateway,
            final NotificationManager notificationManager
    ) {

        this.environmentGateway = environmentGateway;
        this.subscriptionGateway = subscriptionGateway;
        this.actionsGateway = actionsGateway;
        this.notificationManager = notificationManager;
    }

    public void reserve(final String userName, final int applicationId) {
        try {
            final BroadcastInformation information = subscriptionGateway.reserve(userName, applicationId);
            fireNotification(information);
        } catch (Exception exc) {
            LOGGER.error("Reserve failed", exc);
        }
    }

    private void fireNotification(final BroadcastInformation<?> information) {
        LOGGER.debug("New notification to be fired: {}", information);
        notificationManager.pushToClients(information.getSubscribers(), information.getValue());
    }

    public void subscribe(final String userName, final int serverId) {
        try {
            final BroadcastInformation information = subscriptionGateway.subscribe(userName, serverId);
            fireNotification(information);
        } catch (Exception exc) {
            LOGGER.error("Subscribtion failed", exc);
        }
    }

    public void unsubscribe(final String userName, final int serverId) {
        try {
            final BroadcastInformation information = subscriptionGateway.unsubscribe(userName, serverId);
            fireNotification(information);
        } catch (Exception exc) {
            LOGGER.error("Unsubscribtion failed", exc);
        }
    }

    public void free(final String userName, final int applicationId) {
        try {
            final BroadcastInformation information = subscriptionGateway.free(userName, applicationId);
            fireNotification(information);
        } catch (Exception exc) {
            LOGGER.error("Free failed", exc);
        }
    }

    public EnvironmentsInfo status() {
        return environmentGateway.status();
    }

    public ResourceActionsHistory resourceActions(final int applicationId, final Range<Instant> range) {
        return actionsGateway.getActionsOnResource(applicationId, range);
    }

    public ServerActionsHistory serverActions(final int serverId, final Range<Instant> range) {
        return actionsGateway.getActionsOnServer(serverId, range);
    }

    public void newServerAction(final String userName, final int serverId, final String desc) {
        final BroadcastInformation information = actionsGateway.newServerAction(userName, serverId, desc);
        fireNotification(information);
    }

    public void newResourceAction(final String userName, final int applicationId, final String desc) {
        final BroadcastInformation information = actionsGateway.newResourceAction(userName, applicationId, desc);
        fireNotification(information);
    }

    public void newResourceAction(
            final String userName,
            final ResourceDescription resourceDescription,
            final String desc
    ) {
        final BroadcastInformation information = actionsGateway.newResourceAction(userName, resourceDescription, desc);
        fireNotification(information);
    }
}
