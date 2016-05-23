package com.home.teamnotifier.db;


import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract class ActionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final Integer id;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private final UserEntity actor;

    @Column(nullable = false)
    private final Instant timestamp;

    @Column(nullable = false)
    @Size(min = 1)
    private final String details;

    ActionEntity() {
        id = null;
        timestamp = null;
        details = null;
        actor = null;
    }

    ActionEntity(final UserEntity actor, final String details) {
        this.id = null;
        this.actor = actor;
        this.details = details;
        this.timestamp = Instant.now();
    }

    public final UserEntity getActor() {
        return actor;
    }

    public final Instant getTimestamp() {
        return timestamp;
    }

    public final String getDetails() {
        return details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionEntity that = (ActionEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(actor, that.actor) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, actor, timestamp, details);
    }
}
