package com.home.teamnotifier.core.responses.action;

import com.fasterxml.jackson.databind.*;
import com.google.common.collect.Lists;
import com.home.teamnotifier.core.responses.action.ActionInfo;
import com.home.teamnotifier.core.responses.action.ActionsInfo;
import com.home.teamnotifier.utils.Iso8601DateTimeHelper;
import io.dropwizard.jackson.Jackson;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.home.teamnotifier.utils.Iso8601DateTimeHelper.*;
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
    return new ActionInfo("user", parseTimestamp("2015-11-05T23:44:40.220Z"), "description");
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