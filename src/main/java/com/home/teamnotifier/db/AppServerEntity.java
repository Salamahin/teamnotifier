package com.home.teamnotifier.db;

import com.google.common.collect.ImmutableList;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Table(schema = "teamnotifier", name = "AppServer")
public final class AppServerEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private final Integer id;

    @Column(nullable = false)
    private final String name;

    @ManyToOne(optional = false)
    @SuppressWarnings("unused")
    private final EnvironmentEntity environment;

    @Column
    private final String statusUrl;

    @OneToMany(mappedBy = "appServer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final List<SharedResourceEntity> resources;

    @OneToMany(mappedBy = "appServer", fetch = FetchType.EAGER, cascade = CascadeType.ALL,
            orphanRemoval = true)
    private final List<SubscriptionEntity> subscriptions;

    //for hibernate
    private AppServerEntity() {
        id = null;
        name = null;
        environment = null;
        statusUrl = null;
        resources = new ArrayList<>();
        subscriptions = new ArrayList<>();
    }

    AppServerEntity(final EnvironmentEntity environment, final String name) {
        this(environment, name, null);
    }

    AppServerEntity(final EnvironmentEntity environment, final String name, final String checkUrl) {
        this.id = null;
        this.statusUrl = checkUrl;
        this.environment = environment;
        this.name = name;
        this.resources = new ArrayList<>();
        subscriptions = new ArrayList<>();
    }

    public void newSharedResource(final String name) {
        final SharedResourceEntity entity = new SharedResourceEntity(this, name);
        resources.add(entity);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<SharedResourceEntity> getImmutableListOfResources() {
        return ImmutableList.copyOf(resources);
    }

    public List<String> getImmutableListOfSubscribers() {
        return ImmutableList.copyOf(subscriptions.stream()
                .map(s -> s.getSubscriber().getName())
                .collect(Collectors.toList())
        );
    }

    public EnvironmentEntity getEnvironment() {
        return environment;
    }

    public String getStatusURL() {
        return statusUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppServerEntity that = (AppServerEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(resources, that.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, resources);
    }
}
