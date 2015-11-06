package com.home.teamnotifier.db;

import com.google.common.collect.ImmutableList;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(schema="teamnotifier")
public final class AppServerEntity implements Serializable
{
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private final Integer id;

  @Column(nullable=false)
  private final String name;

  @ManyToOne(optional=false)
  private final EnvironmentEntity environment;

  @OneToMany(mappedBy="appServer", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
  private final List<SharedResourceEntity> resources;

  @OneToMany(mappedBy="appServer", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
  private final List<SubscriptionEntity> subscriptions;

  //for hibernate
  private AppServerEntity()
  {
    id=null;
    name=null;
    environment=null;
    resources=new ArrayList<>();
    subscriptions=new ArrayList<>();
  }

  AppServerEntity(final EnvironmentEntity environment, final String name)
  {
    this.id=null;
    this.environment=environment;
    this.name=name;
    this.resources=new ArrayList<>();
    subscriptions=new ArrayList<>();
  }

  public SharedResourceEntity newSharedResource(final String name)
  {
    final SharedResourceEntity entity=new SharedResourceEntity(this, name);
    resources.add(entity);
    return entity;
  }

  public Integer getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public EnvironmentEntity getEnvironment()
  {
    return environment;
  }

  public List<SharedResourceEntity> getImmutableListOfResources()
  {
    return ImmutableList.copyOf(resources);
  }

  public void subscribe(final UserEntity user)
  {
    subscriptions.add(new SubscriptionEntity(this, user));
  }

  public void unsubscribe(final UserEntity user)
  {
    final SubscriptionEntity entity=subscriptions.stream().filter(s -> Objects.equals(user.getId(), s.getSubscriber().getId())).findFirst().get();
    subscriptions.remove(entity);
  }

  public List<SubscriptionData> getImmutableListOfSubscribers()
  {
    return ImmutableList.copyOf(subscriptions.stream()
        .map(s -> new SubscriptionData(s.getSubscriber().getName(), s.getTimestamp()))
        .collect(Collectors.toList())
    );
  }
}
