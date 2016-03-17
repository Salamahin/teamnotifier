package com.home.teamnotifier.core.responses.notification;

import com.fasterxml.jackson.annotation.*;
import com.home.teamnotifier.db.AppServerEntity;
import com.home.teamnotifier.db.UserEntity;

import java.time.Instant;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.ANY)
@JsonTypeName("ActionOnAppServerNotification")
public class ServerAction extends DescribedUserNotification {
    @JsonCreator
    private ServerAction(
            @JsonProperty("actor") final String actor,
            @JsonProperty("targetId") final int targetId,
            @JsonProperty("timestamp") final String timestamp,
            @JsonProperty("description") final String description
    ) {
        super(actor, targetId, timestamp, description);
    }

    public ServerAction(final UserEntity actor, final AppServerEntity target, final String description) {
        this(
                actor.getName(),
                target.getId(),
                Instant.now().toString(),
                description
        );
    }
}
