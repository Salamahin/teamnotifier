package com.home.teamnotifier.db;

import java.time.Instant;

final class ReservationData {
    private final UserEntity occupier;

    private final Instant occupationTime;

    ReservationData(final UserEntity occupier, final Instant occupationTime) {
        this.occupier = occupier;
        this.occupationTime = occupationTime;
    }

    UserEntity getOccupier() {
        return occupier;
    }

    Instant getOccupationTime() {
        return occupationTime;
    }
}
