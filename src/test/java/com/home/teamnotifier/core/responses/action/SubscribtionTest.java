package com.home.teamnotifier.core.responses.action;

import com.google.common.collect.Sets;
import com.home.teamnotifier.db.ServerEntity;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.home.teamnotifier.core.responses.SerializationTestHelper.testDeserializeFromJson;
import static com.home.teamnotifier.core.responses.SerializationTestHelper.testSerializesToJson;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class SubscribtionTest {
    private static ServerSubscribersInfo serverSubscribersInfo;

    @BeforeClass
    public static void setUp() throws Exception {
        final ServerEntity serverEntity = mock(ServerEntity.class);
        doReturn(1).when(serverEntity).getId();
        doReturn(Sets.newHashSet("user")).when(serverEntity).getImmutableSetOfSubscribers();

        serverSubscribersInfo = new ServerSubscribersInfo(serverEntity);
    }

    @Test
    public void testServerSubscribersInfoSerializesToJSON() throws Exception {
        testSerializesToJson(
                ServerSubscribersInfo.class,
                serverSubscribersInfo,
                "fixtures/serverSubscribersInfo.json"
        );
    }


    @Test
    public void testServerSubscribersInfoDeserializesFromJSON() throws Exception {
        testDeserializeFromJson(
                ServerSubscribersInfo.class,
                serverSubscribersInfo,
                "fixtures/serverSubscribersInfo.json"
        );
    }
}
