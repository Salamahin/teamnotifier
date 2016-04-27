package com.home.teamnotifier.core.responses.action;

import com.fasterxml.jackson.annotation.*;
import com.home.teamnotifier.db.ResourceEntity;
import com.home.teamnotifier.db.ServerEntity;

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
    public ServerActionsHistory(final ServerEntity serverEntity, final List<ActionInfo> actions) {
        this(serverEntity.getId(), actions);
    }

    @JsonCreator
    private ServerActionsHistory(
            @JsonProperty("targetId") final int targetId,
            @JsonProperty("actions") final List<ActionInfo> actions
    ) {
        super(targetId, actions);
    }
}
