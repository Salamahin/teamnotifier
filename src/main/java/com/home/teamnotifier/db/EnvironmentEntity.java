package com.home.teamnotifier.db;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table (schema = "teamnotifier")
public class EnvironmentEntity implements Serializable
{
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;

  @Column(nullable = false, unique = true)
  private String name;

  @OneToMany(mappedBy = "environment", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<AppServerEntity> appServers = new ArrayList<>();

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

  public List<AppServerEntity> getAppServers() {
    return appServers;
  }

  public void setAppServers(final List<AppServerEntity> appServers) {
    this.appServers = appServers;
  }
}
