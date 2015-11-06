package com.home.teamnotifier.gateways;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class BroadcastInformation
{
  private final String stringToPush;
  private final List<String> subscribers;

  public BroadcastInformation(final String stringToPush, final List<String> subscribers)
  {
    this.stringToPush=stringToPush;
    this.subscribers=ImmutableList.copyOf(subscribers);
  }

  public String getStringToPush()
  {
    return stringToPush;
  }

  public List<String> getSubscribers()
  {
    return subscribers;
  }
}
