package com.home.teamnotifier.core.responses.action;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

public abstract class AbstractActionsInfo {
    private final List<ActionInfo> actions;

    public AbstractActionsInfo(final List<ActionInfo> actions) {
        this.actions = ImmutableList.copyOf(actions);
    }

    public final List<ActionInfo> getActions() {
        return actions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractActionsInfo that = (AbstractActionsInfo) o;
        return Objects.equals(actions, that.actions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actions);
    }
}
