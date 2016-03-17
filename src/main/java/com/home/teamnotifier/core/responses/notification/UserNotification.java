package com.home.teamnotifier.core.responses.notification;

import java.util.Objects;

abstract class UserNotification extends Notification {
    private final String actor;

    protected UserNotification(
            final String actor,
            final int targetId,
            final String timestamp
    ) {
        super(timestamp, targetId);
        this.actor = actor;
    }

    public final String getActor() {
        return actor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserNotification that = (UserNotification) o;
        return Objects.equals(actor, that.actor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), actor);
    }
}
