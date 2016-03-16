package com.home.teamnotifier.db;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

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
}
