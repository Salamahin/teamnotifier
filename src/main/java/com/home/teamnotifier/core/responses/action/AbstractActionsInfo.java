package com.home.teamnotifier.core.responses.action;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

public abstract class AbstractActionsInfo {
    private final List<ActionInfo> actions;
    private final int targetId;
    private final String from;
    private final String to;

    AbstractActionsInfo(
            final int targetId,
            final String fromTimestamp,
            final String toTimestamp,
            final List<ActionInfo> actions
    ) {
        this.actions = ImmutableList.copyOf(actions);
        this.targetId = targetId;
        this.from = fromTimestamp;
        this.to = toTimestamp;
    }

    public final List<ActionInfo> getActions() {
        return actions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractActionsInfo that = (AbstractActionsInfo) o;
        return targetId == that.targetId &&
                Objects.equals(actions, that.actions) &&
                Objects.equals(from, that.from) &&
                Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actions, targetId, from, to);
    }
}
