package com.home.teamnotifier.db;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(schema = "teamnotifier")
public class AppServerEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column(nullable = false)
  private String name;

  @ManyToOne(optional = false)
  private EnvironmentEntity environment;

  @OneToMany(mappedBy = "appServer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<SharedResourceEntity> resources = new ArrayList<>();

  @OneToMany(mappedBy = "appServerEntity", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<SubscriptionEntity> subscriptions = new ArrayList<>();

  public List<SubscriptionEntity> getSubscriptions() {
    return subscriptions;
  }

  public Integer getId() {
    return id;
  }

  public void setId(final Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public EnvironmentEntity getEnvironment() {
    return environment;
  }

  public void setEnvironment(final EnvironmentEntity environment) {
    this.environment = environment;
  }

  public List<SharedResourceEntity> getResources() {
    return resources;
  }

  public void setResources(final List<SharedResourceEntity> resources) {
    this.resources = resources;
  }
}
