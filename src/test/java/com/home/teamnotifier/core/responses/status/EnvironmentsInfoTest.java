package com.home.teamnotifier.core.responses.status;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.time.Instant;

import static com.home.teamnotifier.core.responses.SerializationTestHelper.testDeserializeFromJson;
import static com.home.teamnotifier.core.responses.SerializationTestHelper.testSerializesToJson;

public class EnvironmentsInfoTest {
    private EnvironmentsInfo createFineInfo() {
        return new EnvironmentsInfo(Lists.newArrayList(createFineEnvironmentInfo()));
    }

    private EnvironmentInfo createFineEnvironmentInfo() {
        return new EnvironmentInfo("environment", Lists.newArrayList(createFineServerInfo()));
    }

    private ServerInfo createFineServerInfo() {
        return new ServerInfo(
                1,
                "server",
                Sets.newHashSet(createFineResourceInfo()),
                Sets.newHashSet("user"),
                true
        );
    }

    private ResourceInfo createFineResourceInfo() {
        return new ResourceInfo(1, "resource", createFineOccupationInfo());
    }

    private OccupationInfo createFineOccupationInfo() {
        return new OccupationInfo("user", Instant.parse("2015-11-05T23:44:40.220Z"));
    }

    @Test
    public void serializesToJSON() throws Exception {
        testSerializesToJson(EnvironmentsInfo.class, createFineInfo(), "fixtures/environmentsInfo.json");
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        testDeserializeFromJson(EnvironmentsInfo.class, createFineInfo(), "fixtures/environmentsInfo.json");
    }
}