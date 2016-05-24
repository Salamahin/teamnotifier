package com.home.teamnotifier.core.responses.action;

import com.fasterxml.jackson.annotation.*;
import com.google.common.collect.ImmutableList;
import com.home.teamnotifier.db.ServerEntity;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.ANY)
@JsonTypeName("ServerSubscribersInfo")
public class ServerSubscribersInfo {
    private final int serverId;
    private final List<String> subscribers;

    public ServerSubscribersInfo(final ServerEntity serverEntity) {
        this(serverEntity.getId(), serverEntity.getImmutableSetOfSubscribers());
    }

    @JsonCreator
    private ServerSubscribersInfo(
            @JsonProperty("serverId") final int serverId,
            @JsonProperty("subscribers") final Collection<String> subscribers
    ) {
        this.serverId = serverId;
        this.subscribers = ImmutableList.copyOf(subscribers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerSubscribersInfo that = (ServerSubscribersInfo) o;
        return serverId == that.serverId &&
                that.subscribers.containsAll(subscribers) &&
                subscribers.containsAll(that.subscribers);
    }

    public List<String> getSubscribers() {
        return subscribers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverId, subscribers);
    }

    @Override
    public String toString() {
        return "ServerSubscribersInfo{" +
                "id=" + serverId +
                ", subscribers=" + subscribers +
                '}';
    }
}
