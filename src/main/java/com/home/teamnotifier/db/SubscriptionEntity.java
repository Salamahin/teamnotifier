package com.home.teamnotifier.db;

import javax.persistence.*;
import java.io.Serializable;
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
    private final Integer id;

    @ManyToOne(optional = false)
    private final ServerEntity server;

    @ManyToOne(optional = false)
    private final UserEntity subscriber;

    //for hibernate
    @SuppressWarnings("unused")
    private SubscriptionEntity() {
        id = null;
        server = null;
        subscriber = null;
    }

    SubscriptionEntity(final ServerEntity server, final UserEntity user) {
        id = null;
        this.server = server;
        this.subscriber = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscriptionEntity that = (SubscriptionEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(server, that.server) &&
                Objects.equals(subscriber, that.subscriber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, server, subscriber);
    }

    UserEntity getSubscriber() {
        return subscriber;
    }

    public ServerEntity getServer() {
        return server;
    }
}
