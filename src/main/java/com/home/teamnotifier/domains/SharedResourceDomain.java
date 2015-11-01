package com.home.teamnotifier.domains;

import javax.persistence.*;

@Entity
@Table(name = "SharedResource")
public class SharedResourceDomain {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;

  @Column
  private String name;

  @ManyToOne
  private AppServerDomain appServer;

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

  public AppServerDomain getAppServer() {
    return appServer;
  }

  public void setAppServer(final AppServerDomain appServer) {
    this.appServer = appServer;
  }
}
