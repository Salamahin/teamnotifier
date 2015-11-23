package com.home.teamnotifier.core.responses.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import static com.home.teamnotifier.utils.Iso8601DateTimeHelper.parseTimestamp;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class NotificationInfoTest {
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    private void serializesToJSON(final BroadcastAction action, final String fixturePath) throws Exception {
        final NotificationInfo userInfo = new NotificationInfo("user", parseTimestamp("2015-11-05T23:44:40.220Z"), action, 1, "details");
        final String expected = MAPPER.writeValueAsString(
                MAPPER.readValue(fixture(fixturePath), NotificationInfo.class)
        );
        assertThat(MAPPER.writeValueAsString(userInfo))
                .isEqualTo(expected);
    }

    private void deserializesFromJSON(final BroadcastAction action, final String fixturePath) throws Exception {
        final NotificationInfo person = new NotificationInfo("user", parseTimestamp("2015-11-05T23:44:40.220Z"), action, 1, "details");
        assertThat(MAPPER.readValue(fixture(fixturePath), NotificationInfo.class))
                .isEqualTo(person);
    }

    @Test
    public void testSubscribeSerialization() throws Exception {
        serializesToJSON(BroadcastAction.SUBSCRIBE, "fixtures/notificationInfoSubscribe.json");
    }

    @Test
    public void testSubscribeDeserialization() throws Exception {
        deserializesFromJSON(BroadcastAction.SUBSCRIBE, "fixtures/notificationInfoSubscribe.json");
    }

    @Test
    public void testUnsubscribeSerialization() throws Exception {
        serializesToJSON(BroadcastAction.UNSUBSCRIBE, "fixtures/notificationInfoUnsubscribe.json");
    }

    @Test
    public void testUnsubscribeDeserialization() throws Exception {
        deserializesFromJSON(BroadcastAction.UNSUBSCRIBE, "fixtures/notificationInfoUnsubscribe.json");
    }

    @Test
    public void testReserveSerialization() throws Exception {
        serializesToJSON(BroadcastAction.RESERVE, "fixtures/notificationInfoReserve.json");
    }

    @Test
    public void testReserveDeserialization() throws Exception {
        deserializesFromJSON(BroadcastAction.RESERVE, "fixtures/notificationInfoReserve.json");
    }

    @Test
    public void testFreeSerialization() throws Exception {
        serializesToJSON(BroadcastAction.FREE, "fixtures/notificationInfoFree.json");
    }

    @Test
    public void testFreeDeserialization() throws Exception {
        deserializesFromJSON(BroadcastAction.FREE, "fixtures/notificationInfoFree.json");
    }

    @Test
    public void testActionOnResourceSerialization() throws Exception {
        serializesToJSON(BroadcastAction.ACTION_ON_RESOURCE, "fixtures/notificationInfoActionOnResource.json");
    }

    @Test
    public void testActionOnResourceDeserialization() throws Exception {
        deserializesFromJSON(BroadcastAction.ACTION_ON_RESOURCE, "fixtures/notificationInfoActionOnResource.json");
    }
}