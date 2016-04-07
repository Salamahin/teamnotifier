package com.home.teamnotifier.core.responses.notification;

import com.home.teamnotifier.db.ServerEntity;
import org.junit.Test;

import static com.home.teamnotifier.core.responses.SerializationTestHelper.testDeserializeFromJson;
import static com.home.teamnotifier.core.responses.SerializationTestHelper.testSerializesToJson;
import static com.home.teamnotifier.core.responses.notification.ReflectionTools.getField;
import static com.home.teamnotifier.core.responses.notification.ReflectionTools.setValueInFinalField;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class ServerStateTest {
    private ServerState getFineState() throws NoSuchFieldException, IllegalAccessException {
        final ServerEntity server = mock(ServerEntity.class);
        doReturn(1).when(server).getId();

        final ServerState state = ServerState.offline(server);
        setValueInFinalField(state, getField(ServerState.class, "timestamp"), "2015-11-05T23:44:40.220Z");
        return state;
    }

    @Test
    public void serializesToJSON() throws Exception {
        testSerializesToJson(ServerState.class, getFineState(), "fixtures/serverStateNotification.json");
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        testDeserializeFromJson(ServerState.class, getFineState(), "fixtures/serverStateNotification.json");
    }
}