package com.home.teamnotifier.db;

import com.google.common.collect.ImmutableList;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(schema="teamnotifier")
public final class EnvironmentEntity implements Serializable
{
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private final Integer id;

  @Column(nullable=false, unique=true)
  private final String name;

  @OneToMany(mappedBy="environment", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
  private final List<AppServerEntity> appServers;

  //for hibernate
  private EnvironmentEntity()
  {
    id=null;
    name=null;
    appServers=new ArrayList<>();
  }

  public EnvironmentEntity(final String name)
  {
    id=null;
    this.name=name;
    appServers=new ArrayList<>();
  }

  public Integer getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public List<AppServerEntity> getImmutableListOfAppServers()
  {
    return ImmutableList.copyOf(appServers);
  }

  public AppServerEntity newAppServer(final String name)
  {
    final AppServerEntity appServerEntity=new AppServerEntity(this, name);
    appServers.add(appServerEntity);
    return appServerEntity;
  }
}
