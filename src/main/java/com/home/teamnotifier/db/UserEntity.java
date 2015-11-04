package com.home.teamnotifier.db;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(schema = "teamnotifier")
public class UserEntity {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;

  @Column
  private String name;

  @OneToMany(mappedBy = "appServer")
  private List<Subscription> subscriptions;

  @OneToMany(mappedBy = "occupier")
  private List<SharedResource> occupiedResources;

  public List<SharedResource> getOccupiedResources() {
    return occupiedResources;
  }

  public void setOccupiedResources(
      final List<SharedResource> occupiedResources) {
    this.occupiedResources = occupiedResources;
  }

  public List<Subscription> getSubscriptions() {
    return subscriptions;
  }

  public void setSubscriptions(final List<Subscription> subscriptions) {
    this.subscriptions = subscriptions;
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
}
