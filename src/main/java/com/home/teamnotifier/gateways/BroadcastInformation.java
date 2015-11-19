package com.home.teamnotifier.gateways;

import com.google.common.collect.ImmutableList;
import com.home.teamnotifier.core.responses.notification.NotificationInfo;

import java.util.List;

public class BroadcastInformation {
  private final NotificationInfo value;

  private final List<String> subscribers;

  public BroadcastInformation(
      final NotificationInfo value,
      final List<String> subscribers
  ) {
    this.value = value;
    this.subscribers = ImmutableList.copyOf(subscribers);
  }

  public NotificationInfo getValue() {
    return value;
  }

  public List<String> getSubscribers() {
    return subscribers;
  }
}
