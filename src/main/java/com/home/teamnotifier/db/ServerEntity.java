package com.home.teamnotifier.db;

import com.google.common.collect.ImmutableSet;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

import static java.util.stream.Collectors.toSet;

@Entity
@Table(schema = "teamnotifier", name = "Server")
public class ServerEntity implements Serializable {
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

    @OneToMany(mappedBy = "server", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private final Set<ResourceEntity> resources;

    /**
     * FIXME
     * Strange hibernate behaviour:
     * when ServereGateway is asked for servers, they have dublicated entities in subscribers
     * The fast fix is to change the collection type to set;
     * seems a bug in hibernate
     * http://stackoverflow.com/questions/7903800/hibernate-inserts-duplicates-into-a-onetomany-collection
     *
     * =========================================
     * NEED TO BE FIXED DUE TO PERFORMANCE NEEDS
     */
    @OneToMany(mappedBy = "server", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<SubscriptionEntity> subscriptions;

    //for hibernate
    @SuppressWarnings("unused")
    private ServerEntity() {
        id = null;
        name = null;
        environment = null;
        statusUrl = null;
        resources = new HashSet<>();
        subscriptions = new HashSet<>();
    }

    ServerEntity(final EnvironmentEntity environment, final String name) {
        this(environment, name, null);
    }

    ServerEntity(final EnvironmentEntity environment, final String name, final String checkUrl) {
        this.id = null;
        this.statusUrl = checkUrl;
        this.environment = environment;
        this.name = name;
        this.resources = new HashSet<>();
        subscriptions = new HashSet<>();
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    Set<ResourceEntity> getImmutableSetOfResources() {
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
        ServerEntity that = (ServerEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(statusUrl, that.statusUrl) &&
                Objects.equals(resources, that.resources);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, statusUrl, resources);
    }

    @Override
    public String toString() {
        return environment.getName() + " " + name;
    }
}
