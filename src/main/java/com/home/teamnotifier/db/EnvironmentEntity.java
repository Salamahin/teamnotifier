package com.home.teamnotifier.db;

import com.google.common.collect.ImmutableSet;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(schema = "teamnotifier", name = "Environment")
public final class EnvironmentEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SuppressWarnings("unused")
    private final Integer id;

    @Column(nullable = false, unique = true)
    private final String name;

    @OneToMany(mappedBy = "environment", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final Set<ServerEntity> servers;

    //for hibernate
    @SuppressWarnings("unused")
    private EnvironmentEntity() {
        id = null;
        name = null;
        servers = new HashSet<>();
    }

    public EnvironmentEntity(final String name) {
        id = null;
        this.name = name;
        servers = new HashSet<>();
    }

    public ServerEntity newServer(final String name) {
        final ServerEntity serverEntity = new ServerEntity(this, name);
        servers.add(serverEntity);
        return serverEntity;
    }

    public ServerEntity newServer(final String name, final String checkUrl) {
        final ServerEntity serverEntity = new ServerEntity(this, name, checkUrl);
        servers.add(serverEntity);
        return serverEntity;
    }

    public String getName() {
        return name;
    }

    public Set<ServerEntity> getImmutableSetOfServers() {
        return ImmutableSet.copyOf(servers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnvironmentEntity that = (EnvironmentEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(servers, that.servers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, servers);
    }
}
