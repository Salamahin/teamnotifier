package com.home.teamnotifier.db;

import javax.persistence.*;
import java.util.List;

@Entity
@Table (schema = "teamnotifier")
public class AppServerEntity implements DataObject {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;

  @Column(nullable = false)
  private String name;

  @ManyToOne(optional = false)
  private EnvironmentEntity environment;

  @OneToMany(mappedBy = "appServer", fetch = FetchType.EAGER)
  private List<SharedResourceEntity> resources;

  @OneToMany(mappedBy = "appServer", fetch = FetchType.EAGER)
  private List<SubscriptionEntity> subscriptions;

  public List<SubscriptionEntity> getSubscriptions() {
    return subscriptions;
  }

  public void setSubscriptions(final List<SubscriptionEntity> subscriptions) {
    this.subscriptions = subscriptions;
  }

  @Override
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
