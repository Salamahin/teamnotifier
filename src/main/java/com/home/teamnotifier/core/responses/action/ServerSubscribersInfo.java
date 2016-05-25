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
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("ServerSubscribersInfo")
public class ServerSubscribersInfo {
    private final int targetId;
    private final List<String> subscribers;

    public ServerSubscribersInfo(final ServerEntity entity) {
        this(entity.getId(), entity.getImmutableSetOfSubscribers());
    }

    @JsonCreator
    private ServerSubscribersInfo(
            @JsonProperty("targetId") final int targetId,
            @JsonProperty("subscribers") final Collection<String> subscribers
    ) {
        this.targetId = targetId;
        this.subscribers = ImmutableList.copyOf(subscribers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerSubscribersInfo that = (ServerSubscribersInfo) o;
        return targetId == that.targetId &&
                Objects.equals(subscribers, that.subscribers);
    }

    public List<String> getSubscribers() {
        return subscribers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetId, subscribers);
    }

    @Override
    public String toString() {
        return "ServerSubscribersInfo{" +
                "targetId=" + targetId +
                ", subscribers=" + subscribers +
                '}';
    }
}
