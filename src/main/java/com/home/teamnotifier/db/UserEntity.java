package com.home.teamnotifier.db;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(schema = "teamnotifier", name = "User")
public class UserEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final Integer id;

    @Column(nullable = false, unique = true)
    @Size(min = 1)
    private final String name;

    @ElementCollection(targetClass = RoleEntity.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "UserInRole", schema = "teamnotifier", joinColumns = @JoinColumn(name = "userId"))
    @Column(name = "RoleId")
    private final Set<RoleEntity> roles;

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
        roles = new HashSet<>();
    }

    public UserEntity(final String name, final String passHash, final String salt) {
        id = null;
        this.name = name;
        this.passHash = passHash;
        this.salt = salt;
        roles = Sets.newHashSet(RoleEntity.USER);
    }

    public Set<RoleEntity> getRolesImmutable() {
        return ImmutableSet.copyOf(roles);
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
