package com.home.teamnotifier.domains;

import javax.persistence.*;

@Entity
@Table(name = "AppServer")
public class AppServerDomain {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;

  @Column
  private String name;

  @ManyToOne
  private EnvironmentDomain environment;

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

  public EnvironmentDomain getEnvironment() {
    return environment;
  }

  public void setEnvironment(final EnvironmentDomain environment) {
    this.environment = environment;
  }
}
