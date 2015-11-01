package com.home.teamnotifier.domains;

import javax.persistence.*;
import java.util.List;

@Entity
@Table (schema = "teamnotifier")
public class Environment implements DataObject {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;

  @Column
  private String name;

  @OneToMany
  private List<AppServer> appServers;

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

  public List<AppServer> getAppServers() {
    return appServers;
  }

  public void setAppServers(final List<AppServer> appServers) {
    this.appServers = appServers;
  }
}
