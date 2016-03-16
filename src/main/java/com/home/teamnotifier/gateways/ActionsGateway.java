package com.home.teamnotifier.gateways;

import com.google.common.collect.Range;
import com.home.teamnotifier.core.BroadcastInformation;
import com.home.teamnotifier.core.responses.action.ActionsInfo;

import java.time.Instant;

public interface ActionsGateway {
    BroadcastInformation newActionOnSharedResource(
            final String userName,
            final int resourceId,
            final String description
    ) throws NoSuchResource, EmptyDescription, NoSuchUser;

    BroadcastInformation newActionOnAppSever(
            final String userName,
            final int serverId,
            final String description
    ) throws NoSuchServer, EmptyDescription, NoSuchUser;

    BroadcastInformation newActionOnSharedResource(
            final String userName,
            final String environmentName,
            final String serverName,
            final String resourceName,
            final String description
    ) throws NoSuchResource, EmptyDescription, NoSuchUser;

    ActionsInfo getActions(final int resourceId, final Range<Instant> range) throws NoSuchResource;
}
