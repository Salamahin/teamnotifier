package com.home.teamnotifier.db;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(
        schema = "teamnotifier",
        name = "Subscription",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"server_id", "subscriber_id"}, name = "uk_server_id_subscriber_id")
        }
)
final class SubscriptionEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SuppressWarnings("unused")
    private final Integer id;

    @ManyToOne(optional = false, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "server_id")
    @SuppressWarnings("unused")
    private final ServerEntity server;

    @ManyToOne(optional = false, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(name = "subscriber_id")
    private final UserEntity subscriber;

    @Column(nullable = false)
    private final Instant timestamp;

    //for hibernate
    @SuppressWarnings("unused")
    private SubscriptionEntity() {
        id = null;
        server = null;
        subscriber = null;
        timestamp = null;
    }

    SubscriptionEntity(final ServerEntity server, final UserEntity user) {
        id = null;
        this.server = server;
        this.subscriber = user;
        this.timestamp = Instant.now();
    }

    public UserEntity getSubscriber() {
        return subscriber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionEntity that = (SubscriptionEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(server, that.server) &&
                Objects.equals(subscriber, that.subscriber) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, server, subscriber, timestamp);
    }
}
