package com.home.teamnotifier.core;

import com.home.teamnotifier.core.responses.ActionInfo;

import java.util.Collection;

public interface NotificationManager {
  void pushToClients(Collection<String> userNames, ActionInfo message);
}
