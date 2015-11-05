package com.home.teamnotifier.gateways;

import com.fasterxml.jackson.databind.*;
import com.google.common.io.Files;
import com.home.teamnotifier.db.*;
import com.home.teamnotifier.resource.environment.EnvironmentsInfo;
import io.dropwizard.jackson.Jackson;
import org.junit.*;
import java.io.File;
import java.nio.charset.Charset;
import static com.home.teamnotifier.gateways.Commons.*;

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
    final Integer serverId = environment.getAppServers().get(0).getId();
    final Integer resourceId = environment.getAppServers().get(0).getResources().get(0).getId();

    final UserEntity user = createPersistedUserWithRandomPassHash("user");

    subscriptionGateway.subscribe(user.getName(), serverId);
    subscriptionGateway.reserve(user.getName(), resourceId);
  }

  @Test
  public void testOutputJson()
  throws Exception {
    final EnvironmentsInfo status = gateway.status();
    Files.write(MAPPER.writeValueAsString(status), new File("out"), Charset.defaultCharset());
  }
}