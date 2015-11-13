package com.home.teamnotifier.core.responses;

import com.fasterxml.jackson.databind.*;
import com.home.teamnotifier.core.responses.UserInfo;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class UserInfoTest {
  private static final ObjectMapper MAPPER = Jackson.newObjectMapper()
      .enable(SerializationFeature.INDENT_OUTPUT);

  @Test
  public void serializesToJSON()
  throws Exception {
    final UserInfo userInfo = new UserInfo("user");
    final String expected = MAPPER.writeValueAsString(
        MAPPER.readValue(fixture("fixtures/userInfo.json"), UserInfo.class));
    assertThat(MAPPER.writeValueAsString(userInfo)).isEqualTo(expected);
  }

  @Test
  public void deserializesFromJSON()
  throws Exception {
    final UserInfo person = new UserInfo("user");
    assertThat(MAPPER.readValue(fixture("fixtures/userInfo.json"), UserInfo.class))
        .isEqualTo(person);
  }
}