package com.home.teamnotifier.db;

import javax.persistence.*;

@Entity
@Table(schema = "teamnotifier")
public class SubscriptionEntity implements DataObject {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @ManyToOne(optional = false)
  private AppServerEntity appServer;

  @ManyToOne(optional = false)
  private UserEntity subscriber;

  public AppServerEntity getAppServer() {
    return appServer;
  }

  public void setAppServer(final AppServerEntity appServer) {
    this.appServer = appServer;
  }

  public UserEntity getSubscriber() {
    return subscriber;
  }

  public void setSubscriber(final UserEntity subscriber) {
    this.subscriber = subscriber;
  }

  @Override
  public Integer getId() {
    return id;
  }
}
