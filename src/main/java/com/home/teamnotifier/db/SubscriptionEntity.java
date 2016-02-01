package com.home.teamnotifier.db;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(
        schema = "teamnotifier",
        name = "Subscription",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"appServerId", "subscriberId"}, name ="uk_serverId_subscriberId")
        }
)
final class SubscriptionEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SuppressWarnings("unused")
    private final Integer id;

    @ManyToOne(optional = false, cascade = {CascadeType.DETACH, CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "appServerId")
    @SuppressWarnings("unused")
    private final AppServerEntity appServer;

    @ManyToOne(optional = false, cascade = {CascadeType.DETACH, CascadeType.MERGE,CascadeType.PERSIST})
    @JoinColumn(name = "subscriberId")
    private final UserEntity subscriber;

    @Column(nullable = false)
    private final Instant timestamp;

    //for hibernate
    @SuppressWarnings("unused")
    private SubscriptionEntity() {
        id = null;
        appServer = null;
        subscriber = null;
        timestamp = null;
    }

    SubscriptionEntity(final AppServerEntity server, final UserEntity user) {
        id = null;
        this.appServer = server;
        this.subscriber = user;
        this.timestamp = Instant.now();
    }

    public UserEntity getSubscriber() {
        return subscriber;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
