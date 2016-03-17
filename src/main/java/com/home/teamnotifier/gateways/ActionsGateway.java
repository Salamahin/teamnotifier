package com.home.teamnotifier.gateways;

import com.google.common.collect.Range;
import com.home.teamnotifier.core.BroadcastInformation;
import com.home.teamnotifier.core.responses.action.ActionsInfo;
import com.home.teamnotifier.gateways.exceptions.EmptyDescription;
import com.home.teamnotifier.gateways.exceptions.NoSuchResource;
import com.home.teamnotifier.gateways.exceptions.NoSuchServer;
import com.home.teamnotifier.gateways.exceptions.NoSuchUser;

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
            final ResourceDescription resourceDescription,
            final String description
    ) throws NoSuchResource, EmptyDescription, NoSuchUser;

    ActionsInfo getActionsOnResource(final int resourceId, final Range<Instant> range) throws NoSuchResource;

    ActionsInfo getActionsOnServer(final int serverId, final Range<Instant> range) throws NoSuchResource;
}
