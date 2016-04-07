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

public class ServerActionTest {
    private ServerAction getFineAction() throws NoSuchFieldException, IllegalAccessException {
        final ServerEntity server = mock(ServerEntity.class);
        doReturn(1).when(server).getId();

        final UserEntity userEntity = mock(UserEntity.class);
        doReturn("user").when(userEntity).getName();

        final ServerAction action = new ServerAction(userEntity, server, "action");
        setValueInFinalField(action, getField(ServerAction.class, "timestamp"), "2015-11-05T23:44:40.220Z");
        return action;
    }

    @Test
    public void serializesToJSON() throws Exception {
        testSerializesToJson(ServerAction.class, getFineAction(), "fixtures/serverActionNotification.json");
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        testDeserializeFromJson(ServerAction.class, getFineAction(), "fixtures/serverActionNotification.json");
    }
}
