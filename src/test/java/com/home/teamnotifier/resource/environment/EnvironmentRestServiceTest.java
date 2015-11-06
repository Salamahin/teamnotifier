package com.home.teamnotifier.resource.environment;

import com.google.common.base.Optional;
import com.google.common.io.BaseEncoding;
import com.home.teamnotifier.authentication.*;
import com.home.teamnotifier.resource.ResourceMonitor;
import io.dropwizard.auth.*;
import io.dropwizard.auth.basic.*;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.StrictAssertions.assertThat;
import static org.mockito.Mockito.*;

public class EnvironmentRestServiceTest {

  public static final String ENV_PATH = "/1.0/environment";

  @Rule
  public ResourceTestRule rule = ruleee();

  @Test
  public void authorizedUserCanGetAccessToTeamEnvResource()
  throws Exception {
    final String pass = BaseEncoding.base64().encode("omg:pass".getBytes());
    final EnvironmentsInfo environments = rule.getJerseyTest()
        .target(ENV_PATH)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .header("Authorization", "Bearer " + pass)
        .get(EnvironmentsInfo.class);
    assertThat(environments).isNotNull();
  }

  @Test
  public void notAuthorizedUsersCannotGetAccess()
  throws Exception {
    final Response envResponse = rule.getJerseyTest().target(ENV_PATH)
        .request().get();
    assertThat(envResponse.getStatus()).isEqualTo(401);
  }

  private ResourceTestRule ruleee() {
    final ResourceMonitor resourceMonitor = mock(ResourceMonitor.class);
    when(resourceMonitor.status()).thenReturn(new EnvironmentsInfo(new ArrayList<>()));

    return ResourceTestRule
        .builder()
        .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
        .addProvider(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
            .setAuthenticator(authenticator())
            .setAuthorizer(new TrivialAuthorizer())
            .setRealm("realm")
            .setPrefix("Bearer")
            .buildAuthFilter()))
        .addProvider(RolesAllowedDynamicFeature.class)
        .addProvider(new AuthValueFactoryProvider.Binder<>(User.class))
        .addResource(new EnvironmentRestService(resourceMonitor))
        .build();
  }

  private Authenticator<BasicCredentials, User> authenticator() {
    return credentials -> {
      if (credentials.getUsername().equals("omg")) {
        return Optional.of(new User("best"));
      } else {
        return Optional.absent();
      }
    };
  }
}