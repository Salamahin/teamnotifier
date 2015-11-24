package com.home.teamnotifier.gateways;

import com.google.common.collect.Range;
import com.home.teamnotifier.core.responses.action.ActionsInfo;

import java.time.Instant;

public interface SharedResourceActionsGateway {
    BroadcastInformation newAction(
            final String userName,
            final int resourceId,
            final String description
    ) throws NoSuchResource, EmptyDescription, NoSuchUser;

    ActionsInfo getActions(final int resourceId, final Range<Instant> range) throws NoSuchResource;
}
