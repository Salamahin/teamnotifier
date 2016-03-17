package com.home.teamnotifier.core.responses.notification;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.home.teamnotifier.db.AppServerEntity;

import java.time.Instant;
import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.ANY)
@JsonTypeName("ServerStateNotification")
public class ServerState extends Notification {
    private final boolean online;

    @SuppressWarnings("unused")
    private ServerState(
            @JsonProperty("timestamp") final String timestamp,
            @JsonProperty("targetId") final int targetId,
            @JsonProperty("online") final boolean online
    ) {
        super(timestamp, targetId);
        this.online = online;
    }

    private ServerState(final AppServerEntity server, final boolean isOnline) {
        super(Instant.now().toString(), server.getId());
        online = isOnline;
    }

    public static ServerState online(final AppServerEntity appServerEntity) {
        return new ServerState(appServerEntity, true);
    }

    public static ServerState offline(final AppServerEntity appServerEntity) {
        return new ServerState(appServerEntity, false);
    }

    public final boolean isOnline() {
        return online;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ServerState that = (ServerState) o;
        return online == that.online;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), online);
    }
}