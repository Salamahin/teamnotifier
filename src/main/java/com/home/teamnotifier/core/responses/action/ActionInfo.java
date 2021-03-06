package com.home.teamnotifier.core.responses.action;

import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.ANY)
@JsonTypeName("ActionInfo")
public class ActionInfo implements Serializable {
    private final String actor;

    private final String timestamp;

    private final String description;

    @JsonCreator
    private ActionInfo(
            @JsonProperty("actor") final String userName,
            @JsonProperty("timestamp") final String timestamp,
            @JsonProperty("description") final String description
    ) {
        this.actor = userName;
        this.timestamp = timestamp;
        this.description = description;
    }

    public ActionInfo(
            final String userName,
            final Instant timestamp,
            final String description
    ) {
        this(userName, timestamp.toString(), description);
    }


    public Instant getTimestamp() {
        return ZonedDateTime.parse(timestamp).toInstant();
    }

    @Override
    public int hashCode() {
        return Objects.hash(actor, timestamp, description);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ActionInfo that = (ActionInfo) o;
        return Objects.equals(actor, that.actor) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(description, that.description);
    }
}
