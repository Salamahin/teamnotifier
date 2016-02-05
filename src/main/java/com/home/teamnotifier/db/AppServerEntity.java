package com.home.teamnotifier.db;

import com.google.common.collect.ImmutableSet;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

import static java.util.stream.Collectors.toSet;

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
    private final Set<SharedResourceEntity> resources;

    /**
     * FIXME
     * Strange hibernate behaviour:
     * when AppServereGateway is asked for servers, they have dublicated entities in subscribers
     * The fast fix is to change the collection type to set;
     * seems a bug in hibernate
     * http://stackoverflow.com/questions/7903800/hibernate-inserts-duplicates-into-a-onetomany-collection
     *
     * =========================================
     * NEED TO BE FIXED DUE TO PERFORMANCE NEEDS
     */
    @OneToMany(mappedBy = "appServer", fetch = FetchType.EAGER, cascade = CascadeType.ALL,
            orphanRemoval = true)
    private final Set<SubscriptionEntity> subscriptions;

    //for hibernate
    private AppServerEntity() {
        id = null;
        name = null;
        environment = null;
        statusUrl = null;
        resources = new HashSet<>();
        subscriptions = new HashSet<>();
    }

    AppServerEntity(final EnvironmentEntity environment, final String name) {
        this(environment, name, null);
    }

    AppServerEntity(final EnvironmentEntity environment, final String name, final String checkUrl) {
        this.id = null;
        this.statusUrl = checkUrl;
        this.environment = environment;
        this.name = name;
        this.resources = new HashSet<>();
        subscriptions = new HashSet<>();
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

    public Set<SharedResourceEntity> getImmutableSetOfResources() {
        return ImmutableSet.copyOf(resources);
    }

    public Set<String> getImmutableSetOfSubscribers() {
        return ImmutableSet.copyOf(subscriptions.stream()
                .map(s -> s.getSubscriber().getName())
                .collect(toSet())
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
                Objects.equals(statusUrl, that.statusUrl) &&
                Objects.equals(resources, that.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, statusUrl, resources);
    }
}
