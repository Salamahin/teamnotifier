package com.home.teamnotifier.db;

import com.google.common.collect.ImmutableList;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private final List<AppServerEntity> appServers;

    //for hibernate
    @SuppressWarnings("unused")
    private EnvironmentEntity() {
        id = null;
        name = null;
        appServers = new ArrayList<>();
    }

    public EnvironmentEntity(final String name) {
        id = null;
        this.name = name;
        appServers = new ArrayList<>();
    }

    public AppServerEntity newAppServer(final String name) {
        final AppServerEntity appServerEntity = new AppServerEntity(this, name);
        appServers.add(appServerEntity);
        return appServerEntity;
    }

    public AppServerEntity newAppServer(final String name, final String checkUrl) {
        final AppServerEntity appServerEntity = new AppServerEntity(this, name, checkUrl);
        appServers.add(appServerEntity);
        return appServerEntity;
    }

    public String getName() {
        return name;
    }

    public List<AppServerEntity> getImmutableListOfAppServers() {
        return ImmutableList.copyOf(appServers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnvironmentEntity that = (EnvironmentEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(appServers, that.appServers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, appServers);
    }
}
