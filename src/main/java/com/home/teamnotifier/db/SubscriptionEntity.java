package com.home.teamnotifier.db;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(schema = "teamnotifier", name = "Subscription")
public final class SubscriptionEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private final Integer id;

  @ManyToOne(optional = false, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST})
  private final AppServerEntity appServer;

  @ManyToOne(optional = false, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST})
  private final UserEntity subscriber;

  @Column(nullable = false)
  private final LocalDateTime timestamp;

  //for hibernate
  private SubscriptionEntity() {
    id = null;
    appServer = null;
    subscriber = null;
    timestamp = null;
  }

  SubscriptionEntity(final AppServerEntity server, final UserEntity user) {
    id = null;
    this.appServer = server;
    this.subscriber = user;
    this.timestamp = LocalDateTime.now();
  }

  public Integer getId() {
    return id;
  }

  public UserEntity getSubscriber() {
    return subscriber;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }
}
