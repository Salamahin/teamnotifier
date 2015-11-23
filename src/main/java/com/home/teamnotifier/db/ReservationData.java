package com.home.teamnotifier.db;

import java.time.Instant;

final class ReservationData {
    private final UserEntity occupier;

    private final Instant occupationTime;

    ReservationData(final UserEntity occupier, final Instant occupationTime) {
        this.occupier = occupier;
        this.occupationTime = occupationTime;
    }

    public UserEntity getOccupier() {
        return occupier;
    }

    public Instant getOccupationTime() {
        return occupationTime;
    }
}
