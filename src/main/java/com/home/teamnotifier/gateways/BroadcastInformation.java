package com.home.teamnotifier.gateways;

import com.google.common.collect.ImmutableList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BroadcastInformation {
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-YYYY");

  private final String stringToPush;

  private final List<String> subscribers;

  public BroadcastInformation(
      final String stringToPush,
      final LocalDateTime timestamp,
      final List<String> subscribers
  ) {
    this.stringToPush = String.format("[%s] %s", formatter.format(timestamp), stringToPush);
    this.subscribers = ImmutableList.copyOf(subscribers);
  }

  public String getStringToPush() {
    return stringToPush;
  }

  public List<String> getSubscribers() {
    return subscribers;
  }
}
