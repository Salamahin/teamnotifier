package com.home.teamnotifier.gateways;

import com.google.common.collect.ImmutableList;
import java.time.LocalDateTime;
import java.util.List;

public class BroadcastInformation {
  private final String stringToPush;

  private final List<String> subscribers;

  private final LocalDateTime timestamp;

  public BroadcastInformation(final String stringToPush, final LocalDateTime timestamp,
      final List<String> subscribers) {
    this.stringToPush = stringToPush;
    this.timestamp = timestamp;
    this.subscribers = ImmutableList.copyOf(subscribers);
  }

  public String getStringToPush() {
    return stringToPush;
  }

  public List<String> getSubscribers() {
    return subscribers;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }
}
