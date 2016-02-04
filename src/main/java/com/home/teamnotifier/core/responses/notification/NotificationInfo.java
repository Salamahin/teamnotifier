package com.home.teamnotifier.core.responses.notification;

import com.fasterxml.jackson.annotation.*;

import java.time.Instant;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("NotificationInfo")
public class NotificationInfo {
    private final String actor;
    private final String timestamp;
    private final EventType event;
    private final int targetId;
    private final String details;

    @JsonCreator
    private NotificationInfo(
            @JsonProperty("actor") final String actor,
            @JsonProperty("timestamp") final String timestamp,
            @JsonProperty("event") final EventType event,
            @JsonProperty("targetId") final int targetId,
            @JsonProperty("details") final String details
    ) {
        this.actor = actor;
        this.timestamp = timestamp;
        this.event = event;
        this.targetId = targetId;
        this.details = details;
    }

    public NotificationInfo(
            final String actor,
            final Instant timestamp,
            final EventType event,
            final int targetId,
            final String details
    ) {
        this(actor, timestamp.toString(), event, targetId, details);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationInfo that = (NotificationInfo) o;
        return targetId == that.targetId &&
                Objects.equals(actor, that.actor) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(details, that.details) &&
                event == that.event;
    }

    @Override
    public int hashCode() {
        return Objects.hash(actor, timestamp, event, targetId, details);
    }

    @Override
    public String toString() {
        return "NotificationInfo{" +
                "actor='" + actor + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", event=" + event +
                ", targetId=" + targetId +
                ", details='" + details + '\'' +
                '}';
    }
}
