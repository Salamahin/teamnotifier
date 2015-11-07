package com.home.teamnotifier.core.environment;

import com.fasterxml.jackson.databind.*;
import com.google.common.collect.Lists;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class ActionsInfoTest {
  private static final ObjectMapper MAPPER = Jackson.newObjectMapper()
      .enable(SerializationFeature.INDENT_OUTPUT);

  @Test
  public void serializesToJSON()
  throws Exception {
    final ActionsInfo info = createFineInfo();
    final String expected = MAPPER.writeValueAsString(
        MAPPER.readValue(fixture("fixtures/actionsInfo.json"), ActionsInfo.class));
    assertThat(MAPPER.writeValueAsString(info)).isEqualTo(expected);
  }

  private ActionsInfo createFineInfo() {
    return new ActionsInfo(Lists.newArrayList(createFineActionInfo()));
  }

  private ActionInfo createFineActionInfo() {
    return new ActionInfo("user", "2015-11-05T23:44:40.220", "description");
  }

  @Test
  public void deserializesFromJSON()
  throws Exception {
    final ActionsInfo info = createFineInfo();

    final ActionsInfo expected = MAPPER.readValue(
        fixture("fixtures/actionsInfo.json"),
        ActionsInfo.class
    );

    assertThat(expected).isEqualTo(info);
  }
}