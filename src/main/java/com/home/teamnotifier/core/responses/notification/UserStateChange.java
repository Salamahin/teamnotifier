package com.home.teamnotifier.core.responses.notification;

import java.util.Objects;

abstract class UserStateChange extends UserNotification {
    private final boolean state;

    UserStateChange(
            final String actor,
            final int targetId,
            final String timestamp,
            final boolean state
    ) {
        super(actor, targetId, timestamp);
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserStateChange that = (UserStateChange) o;
        return state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), state);
    }

    @Override
    public String toString() {
        return "UserStateChange{" +
                "state=" + state +
                "} " + super.toString();
    }
}
