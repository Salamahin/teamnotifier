package com.home.teamnotifier.core.responses.notification;

import java.util.Objects;

public abstract class Notification {
    private final String timestamp;
    private final int targetId;

    protected Notification(final String timestamp, final int targetId) {
        this.timestamp = timestamp;
        this.targetId = targetId;
    }

    public final String getTimestamp() {
        return timestamp;
    }

    public final int getTargetId() {
        return targetId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return targetId == that.targetId &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, targetId);
    }

    @Override
    public String toString() {
        return "Notification{" +
                "timestamp='" + timestamp + '\'' +
                ", targetId=" + targetId +
                '}';
    }
}
