package com.home.teamnotifier.db;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(schema = "teamnotifier", name = "ActionOnSharedResource")
final class ActionOnSharedResourceEntity extends ActionEntity implements Serializable {

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @SuppressWarnings("unused")
    private final SharedResourceEntity resource;

    //for hibernate
    @SuppressWarnings("unused")
    private ActionOnSharedResourceEntity() {
        resource = null;
    }

    public ActionOnSharedResourceEntity(final UserEntity actor,
                                        final SharedResourceEntity sharedResourceEntity,
                                        final String details
    ) {
        super(actor, details);
        this.resource = sharedResourceEntity;
    }
}
