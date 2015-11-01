package com.home.teamnotifier.domains;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Environment")
public class EnvironmentDomain {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;

  @Column
  private String name;

  @OneToMany
  private List<AppServerDomain> appServers;

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

  public List<AppServerDomain> getAppServers() {
    return appServers;
  }

  public void setAppServers(final List<AppServerDomain> appServers) {
    this.appServers = appServers;
  }
}
