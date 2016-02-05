package com.home.teamnotifier.core.responses.status;

import com.fasterxml.jackson.annotation.*;
import com.google.common.collect.ImmutableSet;

import java.util.Objects;
import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("AppServer")
public class AppServerInfo {

    private final String name;

    private final int id;

    private final Set<SharedResourceInfo> resources;

    private final Set<String> subscribers;

    private final Boolean isOnline;

    @JsonCreator
    public AppServerInfo(
            @JsonProperty("id") final int id,
            @JsonProperty("name") final String name,
            @JsonProperty("resources") final Set<SharedResourceInfo> resources,
            @JsonProperty("subscribers") final Set<String> subscribers,
            @JsonProperty("isOnline") final Boolean isOnline
    ) {
        this.name = name;
        this.id = id;
        this.isOnline = isOnline;
        this.resources = ImmutableSet.copyOf(resources);
        this.subscribers = ImmutableSet.copyOf(subscribers);
    }

    public int getId() {
        return id;
    }

    public Set<SharedResourceInfo> getResources() {
        return resources;
    }

    public Set<String> getSubscribers() {
        return subscribers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, resources, subscribers, id, isOnline);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AppServerInfo that = (AppServerInfo) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(resources, that.resources) &&
                Objects.equals(id, that.id) &&
                Objects.equals(subscribers, that.subscribers) &&
                Objects.equals(isOnline, that.isOnline);
    }
}
