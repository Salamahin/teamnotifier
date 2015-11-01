package com.home.teamnotifier.dataobjects;

import javax.persistence.*;
import java.util.List;

@Entity(name = "Environment")
public class EnvironmentDataObject implements DatabaseObject {
  @Id
  private Integer id;

  @Column
  private String name;

  @OneToMany
  private List<AppServerDataObject> appServers;

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

  public List<AppServerDataObject> getAppServers() {
    return appServers;
  }

  public void setAppServers(final List<AppServerDataObject> appServers) {
    this.appServers = appServers;
  }
}
