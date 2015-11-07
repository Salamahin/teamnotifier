package com.home.teamnotifier.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.home.teamnotifier.core.environment.*;
import io.dropwizard.jackson.Jackson;
import org.junit.Before;
import org.junit.Test;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class DbEnvironmentGatewayTest
{

  private static final ObjectMapper MAPPER=Jackson
      .newObjectMapper()
      .enable(SerializationFeature.INDENT_OUTPUT);

  private DbEnvironmentGateway gateway;

  @Before
  public void setUp()
      throws Exception
  {
    gateway=new DbEnvironmentGateway(Commons.HELPER);
    final DbSubscriptionGateway subscriptionGateway=new DbSubscriptionGateway(Commons.HELPER);
    final EnvironmentEntity environment= Commons
        .createPersistedEnvironmentWithOneServerAndOneResource(
        "environment",
        "server",
        "resource"
    );
    final Integer serverId=environment.getImmutableListOfAppServers().get(0).getId();
    final Integer resourceId=environment.getImmutableListOfAppServers().get(0).getImmutableListOfResources().get(0).getId();

    final UserEntity user= Commons.createPersistedUserWithRandomPassHash("user");

    subscriptionGateway.subscribe(user.getName(), serverId);
    subscriptionGateway.reserve(user.getName(), resourceId);
  }

  @Test
  public void testDeserializedFromJson()
      throws Exception
  {
    final EnvironmentsInfo status=gateway.status();
    assertThat(MAPPER.readValue(fixture("fixtures/envConfig.json"), EnvironmentsInfo.class))
        .isEqualTo(status);
  }
}