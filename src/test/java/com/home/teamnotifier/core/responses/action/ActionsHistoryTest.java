package com.home.teamnotifier.core.responses.action;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.time.Instant;

import static com.home.teamnotifier.core.responses.SerializationTestHelper.testDeserializeFromJson;
import static com.home.teamnotifier.core.responses.SerializationTestHelper.testSerializesToJson;

public class ActionsHistoryTest {

    private ResourceActionsHistory createResourceActionsInfo() {
        return new ResourceActionsHistory(Lists.newArrayList(createFineActionInfo()));
    }

    private ServerActionsHistory createActionsOnServerInfo() {
        return new ServerActionsHistory(Lists.newArrayList(createFineActionInfo()));
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