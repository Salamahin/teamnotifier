package com.home.teamnotifier.db;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ActionOnSharedResourceEntity that = (ActionOnSharedResourceEntity) o;
        return Objects.equals(resource, that.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), resource);
    }
}
