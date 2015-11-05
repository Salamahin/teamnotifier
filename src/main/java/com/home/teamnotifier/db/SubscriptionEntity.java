package com.home.teamnotifier.db;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(schema="teamnotifier", name = "Subscription")
public class SubscriptionEntity implements Serializable
{
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;

  @ManyToOne(optional=false)
  private AppServerEntity appServerEntity;

  @ManyToOne(optional=false)
  private UserEntity subscriberEntity;

  @Column(nullable = false)
  private LocalDateTime timestamp;

  public UserEntity getSubscriber()
  {
    return subscriberEntity;
  }

  public AppServerEntity getAppServer()
  {
    return appServerEntity;
  }

  public void setSubscriber(UserEntity subscriber)
  {
    subscriberEntity=subscriber;
  }

  public void setAppServer(AppServerEntity appServer)
  {
    appServerEntity=appServer;
  }

  public LocalDateTime getTimestamp()
  {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp)
  {
    this.timestamp=timestamp;
  }

  public Integer getId()
  {
    return id;
  }

  public void setId(Integer id)
  {
    this.id=id;
  }
}
