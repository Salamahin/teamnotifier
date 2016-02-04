package com.home.teamnotifier.core.responses.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import java.time.Instant;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class NotificationInfoTest {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private void serializesToJSON(final EventType action, final String fixturePath) throws Exception {
        final NotificationInfo userInfo = new NotificationInfo(isInfoWithActor(action) ? "user" : null, Instant.parse("2015-11-05T23:44:40.220Z"), action, 1, "details");
        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(fixture(fixturePath), NotificationInfo.class)
        );
        assertThat(MAPPER.writeValueAsString(userInfo)).isEqualTo(expected);
    }

    private boolean isInfoWithActor(EventType action) {
        return action != EventType.SERVER_OFFLINE && action != EventType.SERVER_ONLINE;
    }

    private void deserializesFromJSON(final EventType action, final String fixturePath) throws Exception {
        final NotificationInfo person = new NotificationInfo(isInfoWithActor(action) ? "user" : null, Instant.parse("2015-11-05T23:44:40.220Z"), action, 1, "details");
        assertThat(MAPPER.readValue(fixture(fixturePath), NotificationInfo.class))
                .isEqualTo(person);
    }

    @Test
    public void testSubscribeSerialization() throws Exception {
        serializesToJSON(EventType.SUBSCRIBE, "fixtures/notificationInfoSubscribe.json");
    }

    @Test
    public void testSubscribeDeserialization() throws Exception {
        deserializesFromJSON(EventType.SUBSCRIBE, "fixtures/notificationInfoSubscribe.json");
    }

    @Test
    public void testOnlineSerialization() throws Exception {
        serializesToJSON(EventType.SERVER_ONLINE, "fixtures/notificationInfoServerOnline.json");
    }

    @Test
    public void testOnlineDeserialization() throws Exception {
        deserializesFromJSON(EventType.SERVER_ONLINE, "fixtures/notificationInfoServerOnline.json");
    }

    @Test
    public void testOfflineSerialization() throws Exception {
        serializesToJSON(EventType.SERVER_OFFLINE, "fixtures/notificationInfoServerOffline.json");
    }

    @Test
    public void testOfflineDeserialization() throws Exception {
        deserializesFromJSON(EventType.SERVER_OFFLINE, "fixtures/notificationInfoServerOffline.json");
    }

    @Test
    public void testUnsubscribeSerialization() throws Exception {
        serializesToJSON(EventType.UNSUBSCRIBE, "fixtures/notificationInfoUnsubscribe.json");
    }

    @Test
    public void testUnsubscribeDeserialization() throws Exception {
        deserializesFromJSON(EventType.UNSUBSCRIBE, "fixtures/notificationInfoUnsubscribe.json");
    }

    @Test
    public void testReserveSerialization() throws Exception {
        serializesToJSON(EventType.RESERVE, "fixtures/notificationInfoReserve.json");
    }

    @Test
    public void testReserveDeserialization() throws Exception {
        deserializesFromJSON(EventType.RESERVE, "fixtures/notificationInfoReserve.json");
    }

    @Test
    public void testFreeSerialization() throws Exception {
        serializesToJSON(EventType.FREE, "fixtures/notificationInfoFree.json");
    }

    @Test
    public void testFreeDeserialization() throws Exception {
        deserializesFromJSON(EventType.FREE, "fixtures/notificationInfoFree.json");
    }

    @Test
    public void testActionOnResourceSerialization() throws Exception {
        serializesToJSON(EventType.ACTION_ON_RESOURCE, "fixtures/notificationInfoActionOnResource.json");
    }

    @Test
    public void testActionOnResourceDeserialization() throws Exception {
        deserializesFromJSON(EventType.ACTION_ON_RESOURCE, "fixtures/notificationInfoActionOnResource.json");
    }
}