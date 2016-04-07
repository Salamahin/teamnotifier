package com.home.teamnotifier.core.responses.notification;

import com.home.teamnotifier.db.ServerEntity;
import com.home.teamnotifier.db.UserEntity;
import org.junit.Test;

import static com.home.teamnotifier.core.responses.SerializationTestHelper.testDeserializeFromJson;
import static com.home.teamnotifier.core.responses.SerializationTestHelper.testSerializesToJson;
import static com.home.teamnotifier.core.responses.notification.ReflectionTools.getField;
import static com.home.teamnotifier.core.responses.notification.ReflectionTools.setValueInFinalField;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class SubscriptionTest {

    private Subscription getFineSubscribtion() throws NoSuchFieldException, IllegalAccessException {
        final ServerEntity server = mock(ServerEntity.class);
        doReturn(1).when(server).getId();

        final UserEntity userEntity = mock(UserEntity.class);
        doReturn("user").when(userEntity).getName();

        final Subscription subscription = Subscription.subscribe(userEntity, server);
        setValueInFinalField(subscription,  getField(Reservation.class, "timestamp"), "2015-11-05T23:44:40.220Z");

        return subscription;
    }

    @Test
    public void serializesToJSON() throws Exception {
        testSerializesToJson(Subscription.class, getFineSubscribtion(), "fixtures/subscriptionNotification.json");
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        testDeserializeFromJson(Subscription.class, getFineSubscribtion(), "fixtures/subscriptionNotification.json");
    }
}