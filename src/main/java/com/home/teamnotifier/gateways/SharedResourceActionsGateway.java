package com.home.teamnotifier.gateways;

public interface SharedResourceActionsGateway
{
  void newAction(final String userName, final int resourceId, final String description);
}
