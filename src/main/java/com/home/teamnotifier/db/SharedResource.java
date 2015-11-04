package com.home.teamnotifier.db;

import javax.persistence.*;
import java.time.*;

@Entity
@Table(schema = "teamnotifier")
public class SharedResource implements DataObject {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @Column
  private String name;

  @ManyToOne
  private AppServer appServer;

  @ManyToOne
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

  public AppServer getAppServer() {
    return appServer;
  }

  public void setAppServer(final AppServer appServer) {
    this.appServer = appServer;
  }
}
