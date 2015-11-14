package com.home.teamnotifier.authentication;

import com.fasterxml.jackson.databind.*;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class AuthenticationInfoTest {
  private static final ObjectMapper MAPPER = Jackson.newObjectMapper()
      .enable(SerializationFeature.INDENT_OUTPUT);

  @Test
  public void serializesToJSON()
  throws Exception {
    final AuthenticationInfo info = new AuthenticationInfo("tokenstr");
    final String expected = MAPPER.writeValueAsString(
        MAPPER.readValue(fixture("fixtures/authenticationInfo.json"), AuthenticationInfo.class));
    assertThat(MAPPER.writeValueAsString(info)).isEqualTo(expected);
  }

  @Test
  public void deserializesFromJSON()
  throws Exception {
    final AuthenticationInfo info = new AuthenticationInfo("tokenstr");
    assertThat(
        MAPPER.readValue(fixture("fixtures/authenticationInfo.json"), AuthenticationInfo.class))
        .isEqualTo(info);
  }
}