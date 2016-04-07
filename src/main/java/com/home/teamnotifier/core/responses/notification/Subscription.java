package com.home.teamnotifier.core.responses.notification;

import com.fasterxml.jackson.annotation.*;
import com.home.teamnotifier.db.ServerEntity;
import com.home.teamnotifier.db.UserEntity;

import java.time.Instant;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.ANY)
@JsonTypeName("SubscriptionNotification")
public class Subscription extends UserStateChange {
    @SuppressWarnings("unused")
    @JsonCreator
    private Subscription(
            @JsonProperty("actor") final String actor,
            @JsonProperty("targetId") final int targetId,
            @JsonProperty("timestamp") final String timestamp,
            @JsonProperty("state") final boolean state
    ) {
        super(actor, targetId, timestamp, state);
    }

    private Subscription(final UserEntity actor, final ServerEntity target, final boolean state) {
        super(actor.getName(), target.getId(), Instant.now().toString(), state);
    }

    public static Subscription subscribe(final UserEntity actor, final ServerEntity server) {
        return new Subscription(actor, server, true);
    }

    public static Subscription unsubscribe(final UserEntity actor, final ServerEntity server) {
        return new Subscription(actor, server, false);
    }
}
