package com.home.teamnotifier.db;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(schema = "teamnotifier")
public class UserEntity implements Serializable
{
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private String passHash;

//  @OneToMany(mappedBy = "subscriberEntity", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//  private List<SubscriptionEntity> subscriptions = new ArrayList<>();
//
//  @OneToMany(mappedBy = "occupier", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//  private List<SharedResourceEntity> occupiedResources = new ArrayList<>();

//  public List<SharedResourceEntity> getOccupiedResources() {
//    return occupiedResources;
//  }
//
//  public void setOccupiedResources(
//      final List<SharedResourceEntity> occupiedResources) {
//    this.occupiedResources = occupiedResources;
//  }
//
//  public List<SubscriptionEntity> getSubscriptions() {
//    return subscriptions;
//  }
//
//  public void setSubscriptions(final List<SubscriptionEntity> subscriptions) {
//    this.subscriptions = subscriptions;
//  }

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

  public String getPassHash() {
    return passHash;
  }

  public void setPassHash(final String passHash) {
    this.passHash = passHash;
  }
}
