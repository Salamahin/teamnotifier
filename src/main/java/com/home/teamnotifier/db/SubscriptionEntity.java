package com.home.teamnotifier.db;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(schema = "teamnotifier")
public class SubscriptionEntity implements Serializable
{
  @Id
  private Integer appServerId;

  @Id
  private Integer subscriberId;

  public Integer getAppServerId()
  {
    return appServerId;
  }

  public void setAppServerId(Integer appServerId)
  {
    this.appServerId=appServerId;
  }

  public Integer getSubscriberId()
  {
    return subscriberId;
  }

  public void setSubscriberId(Integer subscriberId)
  {
    this.subscriberId=subscriberId;
  }
}
