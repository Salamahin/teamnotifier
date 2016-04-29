package com.home.teamnotifier.core.responses.action;

import com.fasterxml.jackson.annotation.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.home.teamnotifier.db.ResourceEntity;

import java.time.Instant;
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
    public ResourceActionsHistory(
            final ResourceEntity resourceEntity,
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
    private ResourceActionsHistory(
            @JsonProperty("targetId") final int targetId,
            @JsonProperty("from") final String fromTimestamp,
            @JsonProperty("to") final String toTimestamp,
            @JsonProperty("actions") final List<ActionInfo> actions
    ) {
        super(targetId, fromTimestamp, toTimestamp, actions);
    }
}
