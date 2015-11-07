package com.home.teamnotifier.gateways;

import com.google.common.collect.Range;
import com.home.teamnotifier.core.environment.ActionsInfo;
import java.time.LocalDateTime;

public interface SharedResourceActionsGateway {
  BroadcastInformation newAction(final String userName, final int resourceId,
      final String description);

  ActionsInfo getActions(final int resourceId, final Range<LocalDateTime> range);
}
