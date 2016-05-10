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
@JsonTypeName("EnvironmentsInfo")
public class EnvironmentsInfo {
    private final List<EnvironmentInfo> environments;

    @JsonCreator
    public EnvironmentsInfo(@JsonProperty("environments") final List<EnvironmentInfo> environments) {
        this.environments = ImmutableList.copyOf(environments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(environments);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final EnvironmentsInfo that = (EnvironmentsInfo) o;
        return Objects.equals(environments, that.environments);
    }
}
