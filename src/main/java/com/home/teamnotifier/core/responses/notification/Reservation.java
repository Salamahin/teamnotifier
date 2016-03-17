package com.home.teamnotifier.core.responses.notification;

import com.fasterxml.jackson.annotation.*;
import com.home.teamnotifier.db.SharedResourceEntity;
import com.home.teamnotifier.db.UserEntity;

import java.time.Instant;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.ANY)
@JsonTypeName("ReservationNotification")
public class Reservation extends UserStateChange {

    @SuppressWarnings("unused")
    @JsonCreator
    private Reservation(
            @JsonProperty("actor") final String actor,
            @JsonProperty("targetId") final int targetId,
            @JsonProperty("timestamp") final String timestamp,
            @JsonProperty("state") final boolean state
    ) {
        super(actor, targetId, timestamp, state);
    }

    private Reservation(final UserEntity actor, final SharedResourceEntity target, final boolean state) {
        super(actor.getName(), target.getId(), Instant.now().toString(), state);
    }

    public static Reservation reserve(final UserEntity actor, final SharedResourceEntity resource) {
        return new Reservation(actor, resource, true);
    }

    public static Reservation free(final UserEntity actor, final SharedResourceEntity resource) {
        return new Reservation(actor, resource, false);
    }
}
