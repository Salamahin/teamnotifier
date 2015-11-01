package com.home.teamnotifier.domains;

import javax.persistence.*;

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
