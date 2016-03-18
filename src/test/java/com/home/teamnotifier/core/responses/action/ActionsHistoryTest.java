package com.home.teamnotifier.core.responses.action;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.time.Instant;

import static com.home.teamnotifier.core.responses.SerializationTestHelper.testDeserializeFromJson;
import static com.home.teamnotifier.core.responses.SerializationTestHelper.testSerializesToJson;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class ActionsHistoryTest {

    private ResourceActionsHistory createActionsOnSharedResourceInfo() {
        return new ResourceActionsHistory(Lists.newArrayList(createFineActionInfo()));
    }

    private ServerActionsHistory createActionsOnAppServerInfo() {
        return new ServerActionsHistory(Lists.newArrayList(createFineActionInfo()));
    }

    private ActionInfo createFineActionInfo() {
        return new ActionInfo("user", Instant.parse("2015-11-05T23:44:40.220Z"), "description");
    }


    @Test
    public void actionsOnSharedResourceSerializesToJSON() throws Exception {
       testSerializesToJson(
               ResourceActionsHistory.class,
               createActionsOnSharedResourceInfo(),
               "fixtures/resourceActionsHistory.json"
       );
    }

    @Test
    public void actionsOnServerSerializesToJSON() throws Exception {
        testSerializesToJson(
                ServerActionsHistory.class,
                createActionsOnAppServerInfo(),
                "fixtures/serverActionsHistory.json"
        );
    }

    @Test
    public void actionsOnSharedResourceDeserializesFromJSON() throws Exception {
        testDeserializeFromJson(
                ResourceActionsHistory.class,
                createActionsOnSharedResourceInfo(),
                "fixtures/resourceActionsHistory.json"
        );
    }

    @Test
    public void actionsOnServerDeserializesFromJSON() throws Exception {
        testDeserializeFromJson(
                ServerActionsHistory.class,
                createActionsOnAppServerInfo(),
                "fixtures/serverActionsHistory.json"
        );
    }
}