package com.home.teamnotifier.core.responses.notification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Arrays;

public enum BroadcastAction {
    SUBSCRIBE,
    UNSUBSCRIBE,
    RESERVE,
    FREE,
    ACTION_ON_RESOURCE;

    private static BiMap<String, BroadcastAction> namesMap = HashBiMap.create();

    static {
        Arrays.stream(BroadcastAction.values())
                .forEach(at -> namesMap.put(at.name(), at));
    }

    @JsonCreator
    public static BroadcastAction forValue(String value) {
        return namesMap.get(value);
    }

    @JsonValue
    public String toValue() {
        return namesMap.inverse().get(this);
    }
}
