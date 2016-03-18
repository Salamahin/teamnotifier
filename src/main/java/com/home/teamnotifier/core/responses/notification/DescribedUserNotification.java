package com.home.teamnotifier.core.responses.notification;

import java.util.Objects;

public abstract class DescribedUserNotification extends UserNotification {
    private final String description;

    protected DescribedUserNotification(
            final String actor,
            final int targetId,
            final String description,
            final String timestamp
    ) {
        super(actor, targetId, timestamp);
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DescribedUserNotification that = (DescribedUserNotification) o;
        return Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), description);
    }

    @Override
    public String toString() {
        return "DescribedUserNotification{" +
                "description='" + description + '\'' +
                "} " + super.toString();
    }
}
