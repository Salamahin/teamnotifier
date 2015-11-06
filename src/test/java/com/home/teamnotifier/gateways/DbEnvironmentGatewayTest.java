package com.home.teamnotifier.gateways;

import com.fasterxml.jackson.databind.*;
import com.home.teamnotifier.db.*;
import com.home.teamnotifier.resource.environment.EnvironmentsInfo;
import io.dropwizard.jackson.Jackson;
import org.junit.*;
import static com.home.teamnotifier.gateways.Commons.*;
import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class DbEnvironmentGatewayTest {

  private static final ObjectMapper MAPPER = Jackson
      .newObjectMapper()
      .enable(SerializationFeature.INDENT_OUTPUT);

  private DbEnvironmentGateway gateway;

  @Before
  public void setUp()
  throws Exception {
    gateway = new DbEnvironmentGateway(HELPER);
    final DbSubscriptionGateway subscriptionGateway = new DbSubscriptionGateway(HELPER);
    final EnvironmentEntity environment
        = createPersistedEnvironmentWithOneServerAndOneResource(
        "environment",
        "server",
        "resource"
    );
    final Integer serverId = environment.getImmutableListOfAppServers().get(0).getId();
    final Integer resourceId = environment.getImmutableListOfAppServers().get(0).getImmutableListOfResources().get(0).getId();

    final UserEntity user = createPersistedUserWithRandomPassHash("user");

    subscriptionGateway.subscribe(user.getName(), serverId);
    subscriptionGateway.reserve(user.getName(), resourceId);
  }

  @Test
  public void testDeserializedFromJson()
  throws Exception {
    final EnvironmentsInfo status = gateway.status();
    assertThat(MAPPER.readValue(fixture("fixtures/envConfig.json"), EnvironmentsInfo.class))
        .isEqualTo(status);
  }
}