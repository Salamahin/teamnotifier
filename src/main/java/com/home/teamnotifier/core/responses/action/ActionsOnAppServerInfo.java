package com.home.teamnotifier.core.responses.action;

import com.fasterxml.jackson.annotation.*;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.ANY)
@JsonTypeName("ActionsOnAppServer")
public class ActionsOnAppServerInfo extends AbstractActionsInfo {

    @JsonCreator
    public ActionsOnAppServerInfo(@JsonProperty("actions") final List<ActionInfo> actions) {
        super(actions);
    }
}
