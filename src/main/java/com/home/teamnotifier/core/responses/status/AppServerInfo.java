package com.home.teamnotifier.core.responses.status;

import com.fasterxml.jackson.annotation.*;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

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

    private final List<SharedResourceInfo> resources;

    private final List<String> subscribers;

    @JsonCreator
    public AppServerInfo(
            @JsonProperty("id") final int id,
            @JsonProperty("name") final String name,
            @JsonProperty("resources") final List<SharedResourceInfo> resources,
            @JsonProperty("subscribers") final List<String> subscibers
    ) {
        this.name = name;
        this.id = id;
        this.resources = ImmutableList.copyOf(resources);
        this.subscribers = ImmutableList.copyOf(subscibers);
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public List<SharedResourceInfo> getResources() {
        return resources;
    }

    public List<String> getSubscribers() {
        return subscribers;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, resources, subscribers, id);
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
                Objects.equals(subscribers, that.subscribers);
    }
}
