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
@JsonTypeName("ServerActionsHistory")
public class ServerActionsHistory extends AbstractActionsInfo {

    @JsonCreator
    public ServerActionsHistory(@JsonProperty("actions") final List<ActionInfo> actions) {
        super(actions);
    }
}
