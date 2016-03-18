package com.home.teamnotifier.db;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(schema = "teamnotifier", name = "ActionOnSharedResource")
final class ActionOnSharedResourceEntity extends ActionEntity implements Serializable {

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @SuppressWarnings("unused")
    private final ResourceEntity resource;

    //for hibernate
    @SuppressWarnings("unused")
    private ActionOnSharedResourceEntity() {
        resource = null;
    }

    public ActionOnSharedResourceEntity(final UserEntity actor,
                                        final ResourceEntity resourceEntity,
                                        final String details
    ) {
        super(actor, details);
        this.resource = resourceEntity;
    }
}
