package com.home.teamnotifier.core.responses.notification;

import com.fasterxml.jackson.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("NotificationInfo")
public class NotificationInfo {
    private final String name;
    private final String timestamp;
    private final BroadcastAction action;
    private final int targetId;

    @JsonCreator
    public NotificationInfo(
            @JsonProperty("name") final String name,
            @JsonProperty("timestamp") final String timestamp,
            @JsonProperty("action") final BroadcastAction action,
            @JsonProperty("targetId") final int targetId
    ) {
        this.name = name;
        this.timestamp = timestamp;
        this.action = action;
        this.targetId = targetId;
    }

    public String getName() {
        return name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public BroadcastAction getAction() {
        return action;
    }

    public int getTargetId() {
        return targetId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationInfo that = (NotificationInfo) o;
        return targetId == that.targetId &&
                Objects.equals(name, that.name) &&
                Objects.equals(timestamp, that.timestamp) &&
                action == that.action;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, timestamp, action, targetId);
    }
}
