package com.home.teamnotifier.db;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(schema = "teamnotifier", name = "User")
public final class UserEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final Integer id;

    @Column(nullable = false, unique = true)
    @Size(min = 1)
    private final String name;

    @Column(nullable = false, unique = true)
    @Size(min = 1)
    private final String passHash;

    @Column(nullable = false, unique = true)
    @Size(min = 10)
    private final String salt;

    //for hibernate
    private UserEntity() {
        id = null;
        name = null;
        passHash = null;
        salt = null;
    }

    public UserEntity(final String name, final String passHash, final String salt) {
        id = null;
        this.name = name;
        this.passHash = passHash;
        this.salt = salt;
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

    public String getSalt() {
        return salt;
    }
}
