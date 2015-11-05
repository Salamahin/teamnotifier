package com.home.teamnotifier.db;

import javax.persistence.*;
import java.io.Serializable;
import java.time.*;

@Entity
@Table(schema = "teamnotifier", name = "SharedResource")
public class SharedResourceEntity implements Serializable
{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column(nullable = false)
  private String name;

  @ManyToOne(optional = false)
  private AppServerEntity appServer;

  @ManyToOne(optional = true)
  private UserEntity occupier;

  @Column
  private LocalDateTime occupationStartTime;

  public LocalDateTime getOccupationStartTime() {
    return occupationStartTime;
  }

  public void setOccupationStartTime(final LocalDateTime occupationStartTime) {
    this.occupationStartTime = occupationStartTime;
  }

  public UserEntity getOccupier() {
    return occupier;
  }

  public void setOccupier(final UserEntity occupier) {
    this.occupier = occupier;
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

  public AppServerEntity getAppServer() {
    return appServer;
  }

  public void setAppServer(final AppServerEntity appServer) {
    this.appServer = appServer;
  }
}
