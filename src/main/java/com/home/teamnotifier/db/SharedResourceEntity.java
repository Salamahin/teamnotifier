package com.home.teamnotifier.db;

import com.google.common.base.Preconditions;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(schema="teamnotifier")
public final class SharedResourceEntity implements Serializable
{
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private final Integer id;

  @Column(nullable=false)
  private final String name;

  @ManyToOne(optional=false)
  private final AppServerEntity appServer;

  @ManyToOne(optional=true)
  private UserEntity occupier;

  @Column
  private LocalDateTime occupationStartTime;

  //for hibernate
  private SharedResourceEntity()
  {
    id=null;
    name=null;
    appServer=null;
  }

  SharedResourceEntity(final AppServerEntity appServer, final String name)
  {
    id=null;
    this.name=name;
    this.appServer=appServer;
  }

  public Integer getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public AppServerEntity getAppServer()
  {
    return appServer;
  }

  public void reserve(final UserEntity userEntity)
  {
    Preconditions.checkState(occupier == null);
    occupier=userEntity;
    occupationStartTime=LocalDateTime.now();
  }

  public void free()
  {
    Preconditions.checkState(occupier != null);
    occupier=null;
    occupationStartTime=null;
  }

  public Optional<ReservationData> getReservationData()
  {
    if (occupier != null)
      return Optional.of(new ReservationData(occupier, occupationStartTime));
    else
      return Optional.empty();
  }
}
