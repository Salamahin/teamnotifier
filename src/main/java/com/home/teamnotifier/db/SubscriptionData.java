package com.home.teamnotifier.db;

import java.time.LocalDateTime;

public final class SubscriptionData {
  private final String user;

  private final LocalDateTime subscribtionTime;

  SubscriptionData(final String user, final LocalDateTime subscribtionTime) {
    this.user = user;
    this.subscribtionTime = subscribtionTime;
  }

  public String getUserName() {
    return user;
  }

  public LocalDateTime getSubscribtionTime() {
    return subscribtionTime;
  }
}
