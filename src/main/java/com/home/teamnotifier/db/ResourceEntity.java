package com.home.teamnotifier.db;

import com.google.common.base.Preconditions;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(schema = "teamnotifier", name = "Resource")
public class ResourceEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final Integer id;

    @Column(nullable = false)
    private final String name;

    @ManyToOne(optional = false)
    private final ServerEntity server;

    @ManyToOne(optional = true)
    private UserEntity occupier;

    @Column(nullable = true)
    private Instant occupationStartTime;

    //for hibernate
    private ResourceEntity() {
        id = null;
        name = null;
        server = null;
    }

    ResourceEntity(final ServerEntity server, final String name) {
        id = null;
        this.name = name;
        this.server = server;
    }

    public void reserve(final UserEntity userEntity) {
        Preconditions.checkState(occupier == null);
        occupier = userEntity;
        occupationStartTime = Instant.now();
    }

    public void free() {
        Preconditions.checkState(occupier != null);
        occupier = null;
        occupationStartTime = null;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ServerEntity getServer() {
        return server;
    }

    public Optional<ReservationData> getReservationData() {
        if (occupier != null) {
            return Optional.of(new ReservationData(occupier, occupationStartTime));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceEntity that = (ResourceEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
