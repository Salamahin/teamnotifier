package com.home.teamnotifier.core.responses.notification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Arrays;

public enum EventType
{
    SUBSCRIBE,
    UNSUBSCRIBE,
    RESERVE,
    FREE,
    SERVER_ONLINE,
    SERVER_OFFLINE,
    ACTION_ON_RESOURCE,
    ACTION_ON_SERVER;

    private static final BiMap<String, EventType> namesMap = HashBiMap.create();

    static {
        Arrays.stream(EventType.values())
                .forEach(at -> namesMap.put(at.name(), at));
    }

    @JsonCreator
    @SuppressWarnings("unused")
    public static EventType forValue(String value) {
        return namesMap.get(value);
    }

    @JsonValue
    @SuppressWarnings("unused")
    public String toValue() {
        return namesMap.inverse().get(this);
    }
}
