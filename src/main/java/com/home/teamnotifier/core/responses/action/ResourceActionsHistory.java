package com.home.teamnotifier.core.responses.action;

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
        creatorVisibility = JsonAutoDetect.Visibility.ANY)
@JsonTypeName("ResourceActionsHistory")
public class ResourceActionsHistory extends AbstractActionsInfo{

    @JsonCreator
    public ResourceActionsHistory(@JsonProperty("actions") final List<ActionInfo> actions) {
        super(actions);
    }
}
