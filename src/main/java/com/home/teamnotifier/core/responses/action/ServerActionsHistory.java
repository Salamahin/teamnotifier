package com.home.teamnotifier.core.responses.action;

import com.fasterxml.jackson.annotation.*;
import com.google.common.collect.Range;
import com.home.teamnotifier.db.ServerEntity;

import java.time.Instant;
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
    public ServerActionsHistory(
            final ServerEntity resourceEntity,
            final Range<Instant> timeRange,
            final List<ActionInfo> actions
    ) {
        this(
                resourceEntity.getId(),
                timeRange.lowerEndpoint().toString(),
                timeRange.upperEndpoint().toString(),
                actions
        );
    }
    @JsonCreator
    private ServerActionsHistory(
            @JsonProperty("targetId") final int targetId,
            @JsonProperty("from") final String fromTimestamp,
            @JsonProperty("to") final String toTimestamp,
            @JsonProperty("actions") final List<ActionInfo> actions
    ) {
        super(targetId, fromTimestamp, toTimestamp, actions);
    }
}
