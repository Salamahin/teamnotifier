package com.home.teamnotifier.core.responses.status;

import com.fasterxml.jackson.databind.*;
import com.google.common.collect.*;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class EnvironmentsInfoTest {
  private static final ObjectMapper MAPPER = Jackson.newObjectMapper()
      .enable(SerializationFeature.INDENT_OUTPUT);

  @Test
  public void serializesToJSON()
  throws Exception {
    final EnvironmentsInfo info = createFineInfo();
    final String expected = MAPPER.writeValueAsString(
        MAPPER.readValue(fixture("fixtures/envConfig.json"), EnvironmentsInfo.class));
    assertThat(MAPPER.writeValueAsString(info)).isEqualTo(expected);
  }

  private EnvironmentsInfo createFineInfo() {
    return new EnvironmentsInfo(Lists.newArrayList(createFineEnvironmentInfo()));
  }

  private EnvironmentInfo createFineEnvironmentInfo() {
    return new EnvironmentInfo("environment", Lists.newArrayList(createFineAppServerInfo()));
  }

  private AppServerInfo createFineAppServerInfo() {
    return new AppServerInfo(
        1,
        "server",
            Lists.newArrayList(createFineSharedResourceInfo()),
            Lists.newArrayList("user")
    );
  }

  private SharedResourceInfo createFineSharedResourceInfo() {
    return new SharedResourceInfo(1, "resource", createFineOccupationInfo());
  }

  private OccupationInfo createFineOccupationInfo() {
    return new OccupationInfo("user", "2015-11-05T23:44:40.220");
  }

  @Test
  public void deserializesFromJSON()
  throws Exception {
    final EnvironmentsInfo info = createFineInfo();

    final EnvironmentsInfo expected = MAPPER.readValue(
        fixture("fixtures/envConfig.json"),
        EnvironmentsInfo.class
    );

    assertThat(expected).isEqualTo(info);
  }
}