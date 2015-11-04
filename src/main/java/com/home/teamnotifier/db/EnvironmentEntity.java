package com.home.teamnotifier.db;

import javax.persistence.*;
import java.util.List;

@Entity
@Table (schema = "teamnotifier")
public class EnvironmentEntity implements DataObject {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;

  @Column
  private String name;

  @OneToMany(mappedBy = "environment")
  private List<AppServerEntity> appServers;

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

  public List<AppServerEntity> getAppServers() {
    return appServers;
  }

  public void setAppServers(final List<AppServerEntity> appServers) {
    this.appServers = appServers;
  }
}
