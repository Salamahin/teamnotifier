package com.home.teamnotifier.gateways;

import com.google.common.collect.Range;
import com.home.teamnotifier.core.BroadcastInformation;
import com.home.teamnotifier.core.responses.action.ServerActionsHistory;
import com.home.teamnotifier.core.responses.action.ResourceActionsHistory;
import com.home.teamnotifier.core.responses.notification.ResourceAction;
import com.home.teamnotifier.core.responses.notification.ServerAction;
import com.home.teamnotifier.gateways.exceptions.EmptyDescription;
import com.home.teamnotifier.gateways.exceptions.NoSuchResource;
import com.home.teamnotifier.gateways.exceptions.NoSuchServer;
import com.home.teamnotifier.gateways.exceptions.NoSuchUser;

import java.time.Instant;

public interface ActionsGateway {
    BroadcastInformation<ResourceAction> newActionOnSharedResource(
            final String userName,
            final int resourceId,
            final String description
    ) throws NoSuchResource, EmptyDescription, NoSuchUser;

    BroadcastInformation<ResourceAction> newActionOnSharedResource(
            final String userName,
            final ResourceDescription resourceDescription,
            final String description
    ) throws NoSuchResource, EmptyDescription, NoSuchUser;

    BroadcastInformation<ServerAction> newActionOnAppSever(
            final String userName,
            final int serverId,
            final String description
    ) throws NoSuchServer, EmptyDescription, NoSuchUser;

    ResourceActionsHistory getActionsOnResource(final int resourceId, final Range<Instant> range) throws NoSuchResource;

    ServerActionsHistory getActionsOnServer(final int serverId, final Range<Instant> range) throws NoSuchServer;
}
