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
@JsonTypeName("EnvironmentInfo")
public class EnvironmentInfo {

    private final String name;

    private final List<ServerInfo> servers;

    @JsonCreator
    public EnvironmentInfo(
            @JsonProperty("name") final String name,
            @JsonProperty("servers") final List<ServerInfo> servers
    ) {
        this.name = name;
        this.servers = ImmutableList.copyOf(servers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, servers);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EnvironmentInfo that = (EnvironmentInfo) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(servers, that.servers);
    }
}
