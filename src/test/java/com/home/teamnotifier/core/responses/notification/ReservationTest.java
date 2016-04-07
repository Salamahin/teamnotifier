package com.home.teamnotifier.core.responses.notification;

import com.home.teamnotifier.db.ResourceEntity;
import com.home.teamnotifier.db.UserEntity;
import org.junit.Test;

import static com.home.teamnotifier.core.responses.SerializationTestHelper.testDeserializeFromJson;
import static com.home.teamnotifier.core.responses.SerializationTestHelper.testSerializesToJson;
import static com.home.teamnotifier.core.responses.notification.ReflectionTools.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class ReservationTest {
    private Reservation getFineReservation() throws NoSuchFieldException, IllegalAccessException {
        final ResourceEntity resource = mock(ResourceEntity.class);
        doReturn(1).when(resource).getId();

        final UserEntity userEntity = mock(UserEntity.class);
        doReturn("user").when(userEntity).getName();

        final Reservation reservation = Reservation.reserve(userEntity, resource);
        setValueInFinalField(reservation,  getField(Reservation.class, "timestamp"), "2015-11-05T23:44:40.220Z");

        return reservation;
    }

    @Test
    public void serializesToJSON() throws Exception {
        testSerializesToJson(Reservation.class, getFineReservation(), "fixtures/reservationNotification.json");
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        testDeserializeFromJson(Reservation.class, getFineReservation(), "fixtures/reservationNotification.json");
    }
}
