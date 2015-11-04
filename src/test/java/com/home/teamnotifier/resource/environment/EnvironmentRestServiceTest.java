package com.home.teamnotifier.resource.environment;

import com.google.common.base.Optional;
import com.google.common.io.BaseEncoding;
import com.home.teamnotifier.authentication.TeamNotifierAuthorizer;
import com.home.teamnotifier.authentication.User;
import com.home.teamnotifier.routine.ResourceMonitor;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.testing.junit.ResourceTestRule;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.StrictAssertions.assertThat;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.Rule;
import org.junit.Test;
import static org.mockito.Mockito.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class EnvironmentRestServiceTest {

  public static final String ENV_PATH = "/1.0/environment";

  @Rule
  public ResourceTestRule rule = ruleee();

  @Test
  public void authorizedUserCanGetAccessToTeamEnvResource()
  throws Exception {
    final String pass = BaseEncoding.base64().encode("omg:pass".getBytes());
    final Environments environments = rule.getJerseyTest()
        .target(ENV_PATH)
        .request(MediaType.APPLICATION_JSON_TYPE)
        .header("Authorization", "Bearer " + pass)
        .get(Environments.class);
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
    when(resourceMonitor.getStatus(1)).thenReturn(emptyList());

    return ResourceTestRule
        .builder()
        .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
        .addProvider(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
            .setAuthenticator(authenticator())
            .setAuthorizer(new TeamNotifierAuthorizer())
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
        return Optional.of(new User(1, "best"));
      } else {
        return Optional.absent();
      }
    };
  }
}