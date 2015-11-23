package com.home.teamnotifier.db;

import java.time.Instant;

final class SubscriptionData {
  private final String user;

  private final Instant subscribtionTime;

  SubscriptionData(final String user, final Instant subscribtionTime) {
    this.user = user;
    this.subscribtionTime = subscribtionTime;
  }

  public String getUserName() {
    return user;
  }

  public Instant getSubscribtionTime() {
    return subscribtionTime;
  }
}
