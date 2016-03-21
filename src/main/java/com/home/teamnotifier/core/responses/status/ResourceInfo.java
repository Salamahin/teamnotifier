package com.home.teamnotifier.core.responses.status;

import com.fasterxml.jackson.annotation.*;

import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("ResourceInfo")
public class ResourceInfo {

    private final int id;

    private final String name;

    private final OccupationInfo occupationInfo;

    @JsonCreator
    public ResourceInfo(
            @JsonProperty("id") final int id, @JsonProperty("name") final String name,
            @JsonProperty("occupationInfo") final OccupationInfo occupationInfo
    ) {
        this.id = id;
        this.name = name;
        this.occupationInfo = occupationInfo;
    }

    public int getId() {
        return id;
    }

    public OccupationInfo getOccupationInfo() {
        return occupationInfo;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, occupationInfo, id);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ResourceInfo that = (ResourceInfo) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(id, that.id) &&
                Objects.equals(occupationInfo, that.occupationInfo);
    }
}
