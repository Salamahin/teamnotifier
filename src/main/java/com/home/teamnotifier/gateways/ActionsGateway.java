package com.home.teamnotifier.gateways;

import com.google.common.collect.Range;
import com.home.teamnotifier.core.BroadcastInformation;
import com.home.teamnotifier.core.responses.action.ActionsOnAppServerInfo;
import com.home.teamnotifier.core.responses.action.ActionsOnSharedResourceInfo;
import com.home.teamnotifier.core.responses.notification.ServerAction;
import com.home.teamnotifier.core.responses.notification.SharedResourceAction;
import com.home.teamnotifier.gateways.exceptions.EmptyDescription;
import com.home.teamnotifier.gateways.exceptions.NoSuchResource;
import com.home.teamnotifier.gateways.exceptions.NoSuchServer;
import com.home.teamnotifier.gateways.exceptions.NoSuchUser;

import java.time.Instant;

public interface ActionsGateway {
    BroadcastInformation<SharedResourceAction> newActionOnSharedResource(
            final String userName,
            final int resourceId,
            final String description
    ) throws NoSuchResource, EmptyDescription, NoSuchUser;

    BroadcastInformation<SharedResourceAction> newActionOnSharedResource(
            final String userName,
            final ResourceDescription resourceDescription,
            final String description
    ) throws NoSuchResource, EmptyDescription, NoSuchUser;

    BroadcastInformation<ServerAction> newActionOnAppSever(
            final String userName,
            final int serverId,
            final String description
    ) throws NoSuchServer, EmptyDescription, NoSuchUser;

    ActionsOnSharedResourceInfo getActionsOnResource(final int resourceId, final Range<Instant> range) throws NoSuchResource;

    ActionsOnAppServerInfo getActionsOnServer(final int serverId, final Range<Instant> range) throws NoSuchServer;
}
