package com.home.teamnotifier.core.responses.action;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.home.teamnotifier.db.ResourceEntity;
import com.home.teamnotifier.db.ServerEntity;
import org.junit.BeforeClass;
import org.junit.Test;

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
        final Range<Instant> timeRange = Range.closed(
                Instant.parse("2015-11-05T23:44:40.220Z"),
                Instant.parse("2015-11-05T23:45:40.220Z")
        );

        return new ResourceActionsHistory(resourceEntity, timeRange, Lists.newArrayList(createFineActionInfo()));
    }

    private ServerActionsHistory createActionsOnServerInfo() {
        final Range<Instant> timeRange = Range.closed(
                Instant.parse("2015-11-05T23:44:40.220Z"),
                Instant.parse("2015-11-05T23:45:40.220Z")
        );

        return new ServerActionsHistory(serverEntity, timeRange, Lists.newArrayList(createFineActionInfo()));
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