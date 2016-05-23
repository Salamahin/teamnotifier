package com.home.teamnotifier.db;


import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(schema = "teamnotifier", name = "ServerAction")
public class ServerActionEntity extends ActionEntity implements Serializable {
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    private final ServerEntity server;

    //for hibernate
    @SuppressWarnings("unused")
    private ServerActionEntity() {
        server = null;
    }

    ServerActionEntity(final UserEntity actor,
                       final ServerEntity server,
                       final String details
    ) {
        super(actor, details);
        this.server = server;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ServerActionEntity that = (ServerActionEntity) o;
        return Objects.equals(server, that.server);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), server);
    }
}
