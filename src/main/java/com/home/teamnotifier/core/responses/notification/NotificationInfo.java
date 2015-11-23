package com.home.teamnotifier.core.responses.notification;

import com.fasterxml.jackson.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.home.teamnotifier.utils.Iso8601DateTimeHelper.*;

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
    private final String details;

    @JsonCreator
    public NotificationInfo(
            @JsonProperty("name") final String name,
            @JsonProperty("timestamp") final String timestamp,
            @JsonProperty("action") final BroadcastAction action,
            @JsonProperty("targetId") final int targetId,
            @JsonProperty("targetId") final String details
    ) {
        this.name = name;
        this.timestamp = timestamp;
        this.action = action;
        this.targetId = targetId;
        this.details = details;
    }

    public NotificationInfo(
            final String name,
            final LocalDateTime timestamp,
            final BroadcastAction action,
            final int targetId,
            final String details) {
        this.name = name;
        this.details = details;
        this.timestamp = toIso8601String(timestamp);
        this.action = action;
        this.targetId = targetId;
    }

    public String getDetails() {
        return details;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getTimestamp() {
        return parseTimestamp(timestamp);
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
                Objects.equals(details, that.details) &&
                action == that.action;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, timestamp, action, targetId, details);
    }
}
