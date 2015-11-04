package com.home.teamnotifier.db;

import javax.persistence.*;

@Entity
@Table(schema = "teamnotifier")
public class Subscription implements DataObject {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @ManyToOne
  private AppServer appServer;

  @ManyToOne
  private UserEntity subscriber;

  public AppServer getAppServer() {
    return appServer;
  }

  public void setAppServer(final AppServer appServer) {
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
