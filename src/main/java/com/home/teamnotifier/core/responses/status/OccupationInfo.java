package com.home.teamnotifier.core.responses.status;

import com.fasterxml.jackson.annotation.*;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("OccupationInfo")
public class OccupationInfo {
    private final String userName;
    private final String occupationTime;

    @JsonCreator
    @SuppressWarnings("unused")
    public OccupationInfo(
            @JsonProperty("userName") final String userName,
            @JsonProperty("occupationTime") final String occupationTime
    ) {
        this.userName = userName;
        this.occupationTime = occupationTime;
    }

    public OccupationInfo(
            final String userName,
            final Instant occupationTime
    ) {
        this.userName = userName;
        this.occupationTime = occupationTime.toString();
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName, occupationTime);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final OccupationInfo that = (OccupationInfo) o;
        return Objects.equals(userName, that.userName) &&
                Objects.equals(occupationTime, that.occupationTime);
    }
}
