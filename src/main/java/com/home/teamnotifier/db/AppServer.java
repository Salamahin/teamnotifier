package com.home.teamnotifier.db;

import javax.persistence.*;
import java.util.List;

@Entity
@Table (schema = "teamnotifier")
public class AppServer implements DataObject {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;

  @Column
  private String name;

  @ManyToOne
  private Environment environment;

  @OneToMany(mappedBy = "appServer")
  private List<SharedResource> resources;

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

  public Environment getEnvironment() {
    return environment;
  }

  public void setEnvironment(final Environment environment) {
    this.environment = environment;
  }

  public List<SharedResource> getResources() {
    return resources;
  }

  public void setResources(final List<SharedResource> resources) {
    this.resources = resources;
  }
}
