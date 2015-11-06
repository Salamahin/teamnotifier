package com.home.teamnotifier.db;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema="teamnotifier", name="ActionOnSharedResource")
public final class ActionOnSharedResourceEntity
{
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private final Integer id;

  @ManyToOne(optional = false, cascade=CascadeType.ALL)
  private final SharedResourceEntity resource;

  @ManyToOne(optional = false, cascade=CascadeType.ALL)
  private final UserEntity actor;

  @Column(nullable = false)
  private final LocalDateTime actionTime;

  @Column(nullable = false)
  private final String details;

  //for hibernate
  private ActionOnSharedResourceEntity()
  {
    id=null;
    resource=null;
    actionTime=null;
    details=null;
    actor=null;
  }

  public ActionOnSharedResourceEntity(final UserEntity actor, final SharedResourceEntity sharedResourceEntity, final String details)
  {
    id=null;
    this.resource=sharedResourceEntity;
    this.actor=actor;
    this.details=details;
    actionTime=LocalDateTime.now();
  }

  public UserEntity getActor()
  {
    return actor;
  }

  public String getDetails()
  {
    return details;
  }

  public LocalDateTime getActionTime()
  {
    return actionTime;
  }

  public SharedResourceEntity getResource()
  {
    return resource;
  }

  public Integer getId()
  {
    return id;
  }
}