package com.home.teamnotifier.db;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(schema = "teamnotifier", name = "User")
public class UserEntity implements Serializable {
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
    @SuppressWarnings("unused")
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity that = (UserEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(passHash, that.passHash) &&
                Objects.equals(salt, that.salt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, passHash, salt);
    }
}
