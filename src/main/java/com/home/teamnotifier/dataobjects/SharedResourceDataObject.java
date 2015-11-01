package com.home.teamnotifier.dataobjects;

import javax.persistence.*;

@Entity(name = "SharedResource")
public class SharedResourceDataObject implements DatabaseObject {

  @Id
  private Integer id;

  @Column
  private String name;

  @ManyToOne
  private AppServerDataObject appServer;

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

  public AppServerDataObject getAppServer() {
    return appServer;
  }

  public void setAppServer(final AppServerDataObject appServer) {
    this.appServer = appServer;
  }
}
