package com.home.teamnotifier.dataobjects;

import javax.persistence.*;

@Entity(name = "ApplicationServer")
public class AppServerDataObject implements DatabaseObject {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Integer id;

  @Column
  private String name;

  @ManyToOne
  private EnvironmentDataObject environment;

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

  public EnvironmentDataObject getEnvironment() {
    return environment;
  }

  public void setEnvironment(final EnvironmentDataObject environment) {
    this.environment = environment;
  }
}
