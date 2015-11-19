package com.home.teamnotifier.core.responses.notification;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

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
    private final String targetName;

    public NotificationInfo(final String name,
                            final String timestamp,
                            final BroadcastAction action,
                            final String targetName
    ) {
        this.name = name;
        this.timestamp = timestamp;
        this.action = action;
        this.targetName = targetName;
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

    public String getTargetName() {
        return targetName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationInfo that = (NotificationInfo) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(timestamp, that.timestamp) &&
                action == that.action &&
                Objects.equals(targetName, that.targetName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, timestamp, action, targetName);
    }
}
