package com.home.teamnotifier.core.responses.action;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import javax.swing.*;
import java.io.IOException;
import java.time.Instant;

import static com.home.teamnotifier.core.responses.SerializationTestHelper.testDeserializeFromJson;
import static com.home.teamnotifier.core.responses.SerializationTestHelper.testSerializesToJson;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class ActionsInfoTest {

    private ActionsOnSharedResourceInfo createActionsOnSharedResourceInfo() {
        return new ActionsOnSharedResourceInfo(Lists.newArrayList(createFineActionInfo()));
    }

    private ActionsOnAppServerInfo createActionsOnAppServerInfo() {
        return new ActionsOnAppServerInfo(Lists.newArrayList(createFineActionInfo()));
    }

    private ActionInfo createFineActionInfo() {
        return new ActionInfo("user", Instant.parse("2015-11-05T23:44:40.220Z"), "description");
    }


    @Test
    public void actionsOnSharedResourceSerializesToJSON() throws Exception {
       testSerializesToJson(
               ActionsOnSharedResourceInfo.class,
               createActionsOnSharedResourceInfo(),
               "fixtures/actionsOnSharedResource.json"
       );
    }

    @Test
    public void actionsOnServerSerializesToJSON() throws Exception {
        testSerializesToJson(
                ActionsOnAppServerInfo.class,
                createActionsOnAppServerInfo(),
                "fixtures/actionsOnAppServer.json"
        );
    }

    @Test
    public void actionsOnSharedResourceDeserializesFromJSON() throws Exception {
        testDeserializeFromJson(
                ActionsOnSharedResourceInfo.class,
                createActionsOnSharedResourceInfo(),
                "fixtures/actionsOnSharedResource.json"
        );
    }

    @Test
    public void actionsOnServerDeserializesFromJSON() throws Exception {
        testDeserializeFromJson(
                ActionsOnAppServerInfo.class,
                createActionsOnAppServerInfo(),
                "fixtures/actionsOnAppServer.json"
        );
    }
}