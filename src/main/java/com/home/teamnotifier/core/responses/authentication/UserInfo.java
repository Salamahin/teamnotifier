package com.home.teamnotifier.core.responses.authentication;

import com.fasterxml.jackson.annotation.*;

import java.util.Objects;

@SuppressWarnings("FieldCanBeLocal")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("UserInfo")
class UserInfo {

    private final String name;

    @JsonCreator
    UserInfo(@JsonProperty("name") String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final UserInfo userInfo = (UserInfo) o;
        return Objects.equals(name, userInfo.name);
    }
}
