package com.home.teamnotifier.db;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(schema = "teamnotifier", name = "User")
public final class UserEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private final Integer id;

  @Column(nullable = false, unique = true)
  private final String name;

  @Column(nullable = false)
  private final String passHash;

  //for hibernate
  private UserEntity() {
    id = null;
    name = null;
    passHash = null;
  }

  public UserEntity(final String name, final String passHash) {
    id = null;
    this.name = name;
    this.passHash = passHash;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getPassHash() {
    return passHash;
  }
}
