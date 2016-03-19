package com.home.teamnotifier.db;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(schema = "teamnotifier", name = "ActionOnAppServer")
public class ActionOnAppServerEntity extends ActionEntity implements Serializable {
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @SuppressWarnings("unused")
    private final AppServerEntity appServer;

    //for hibernate
    @SuppressWarnings("unused")
    private ActionOnAppServerEntity() {
        appServer = null;
    }

    public ActionOnAppServerEntity(final UserEntity actor,
                                   final AppServerEntity sharedResourceEntity,
                                   final String details
    ) {
        super(actor, details);
        this.appServer = sharedResourceEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ActionOnAppServerEntity that = (ActionOnAppServerEntity) o;
        return Objects.equals(appServer, that.appServer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), appServer);
    }
}
