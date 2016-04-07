package com.home.teamnotifier.core;

import com.google.common.collect.ImmutableList;
import com.home.teamnotifier.core.responses.notification.Notification;

import java.util.List;

public class BroadcastInformation<T extends Notification> {
    private final T value;
    private final List<String> subscribers;

    public BroadcastInformation(
            final T value,
            final List<String> subscribers
    ) {
        this.value = value;
        this.subscribers = ImmutableList.copyOf(subscribers);
    }

    public T getValue() {
        return value;
    }

    public List<String> getSubscribers() {
        return subscribers;
    }

    @Override
    public String toString() {
        return "BroadcastInformation{" +
                "value=" + value +
                ", subscribers=" + subscribers +
                '}';
    }
}
