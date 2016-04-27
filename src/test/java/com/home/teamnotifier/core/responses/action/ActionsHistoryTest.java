package com.home.teamnotifier.core.responses.action;

import com.google.common.collect.Lists;
import com.home.teamnotifier.db.ResourceEntity;
import com.home.teamnotifier.db.ServerEntity;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Instant;

import static com.home.teamnotifier.core.responses.SerializationTestHelper.testDeserializeFromJson;
import static com.home.teamnotifier.core.responses.SerializationTestHelper.testSerializesToJson;
import static org.mockito.Mockito.*;

public class ActionsHistoryTest {
    private static ServerEntity serverEntity;
    private static ResourceEntity resourceEntity;

    @BeforeClass
    public static void setUp() throws Exception {
        serverEntity = mock(ServerEntity.class);
        doReturn(1).when(serverEntity).getId();

        resourceEntity = mock(ResourceEntity.class);
        doReturn(1).when(resourceEntity).getId();
    }

    private ResourceActionsHistory createResourceActionsInfo() {
        return new ResourceActionsHistory(resourceEntity, Lists.newArrayList(createFineActionInfo()));
    }

    private ServerActionsHistory createActionsOnServerInfo() {
        return new ServerActionsHistory(serverEntity, Lists.newArrayList(createFineActionInfo()));
    }

    private ActionInfo createFineActionInfo() {
        return new ActionInfo("user", Instant.parse("2015-11-05T23:44:40.220Z"), "description");
    }


    @Test
    public void testResourceActionsSerializesToJSON() throws Exception {
       testSerializesToJson(
               ResourceActionsHistory.class,
               createResourceActionsInfo(),
               "fixtures/resourceActionsHistory.json"
       );
    }

    @Test
    public void testServerActionsSerializesToJSON() throws Exception {
        testSerializesToJson(
                ServerActionsHistory.class,
                createActionsOnServerInfo(),
                "fixtures/serverActionsHistory.json"
        );
    }

    @Test
    public void testResourceActionsDeserializesFromJSON() throws Exception {
        testDeserializeFromJson(
                ResourceActionsHistory.class,
                createResourceActionsInfo(),
                "fixtures/resourceActionsHistory.json"
        );
    }

    @Test
    public void testServerActionsDeserializesFromJSON() throws Exception {
        testDeserializeFromJson(
                ServerActionsHistory.class,
                createActionsOnServerInfo(),
                "fixtures/serverActionsHistory.json"
        );
    }
}