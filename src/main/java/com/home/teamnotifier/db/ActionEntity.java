package com.home.teamnotifier.db;


import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.Instant;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract class ActionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SuppressWarnings("unused")
    private final Integer id;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private final UserEntity actor;

    @Column(nullable = false)
    private final Instant actionTime;

    @Column(nullable = false)
    @Size(min = 1)
    private final String details;

    public ActionEntity() {
        id = null;
        actionTime = null;
        details = null;
        actor = null;
    }

    protected ActionEntity(final UserEntity actor, final String details) {
        this.id = null;
        this.actor = actor;
        this.details = details;
        this.actionTime = Instant.now();
    }

    public final Integer getId() {
        return id;
    }

    public final UserEntity getActor() {
        return actor;
    }

    public final Instant getActionTime() {
        return actionTime;
    }

    public final String getDetails() {
        return details;
    }
}
