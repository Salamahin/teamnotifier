package com.home.teamnotifier.db;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(schema = "teamnotifier", name = "ActionOnSharedResource")
public final class ActionOnSharedResourceEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final Integer id;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private final SharedResourceEntity resource;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private final UserEntity actor;

    @Column(nullable = false)
    private final Instant actionTime;

    @Column(nullable = false)
    @Size(min = 1)
    private final String details;

    //for hibernate
    private ActionOnSharedResourceEntity() {
        id = null;
        resource = null;
        actionTime = null;
        details = null;
        actor = null;
    }

    public ActionOnSharedResourceEntity(final UserEntity actor,
                                        final SharedResourceEntity sharedResourceEntity,
                                        final String details
    ) {
        id = null;
        this.resource = sharedResourceEntity;
        this.actor = actor;
        this.details = details;
        actionTime = Instant.now();
    }

    public UserEntity getActor() {
        return actor;
    }

    public String getDetails() {
        return details;
    }

    public Instant getActionTime() {
        return actionTime;
    }

    public SharedResourceEntity getResource() {
        return resource;
    }

    public Integer getId() {
        return id;
    }
}
