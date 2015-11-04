//package com.home.teamnotifier.resource.environment;
//
//import com.google.common.base.Optional;
//import com.home.teamnotifier.authentication.TeamNotifierAuthenticator;
//import com.home.teamnotifier.authentication.User;
//import com.home.teamnotifier.routine.ResourceMonitor;
//import io.dropwizard.auth.AuthDynamicFeature;
//import io.dropwizard.auth.AuthValueFactoryProvider;
//import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
//import io.dropwizard.testing.junit.ResourceTestRule;
//import static org.assertj.core.api.StrictAssertions.assertThat;
//import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import static org.mockito.Matchers.any;
//import static org.mockito.Mockito.*;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//
//public class EnvironmentRestServiceTest {
//  private TeamNotifierAuthenticator authenticator;
//
//  private ResourceMonitor resourceMonitor;
//
//  @Rule
//  public ResourceTestRule rule = ResourceTestRule
//      .builder()
//      .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
//      .addProvider(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
//          .setAuthenticator(authenticator)
//          .setRealm("SUPER SECRET STUFF")
//          .buildAuthFilter()))
//      .addProvider(new AuthValueFactoryProvider.Binder<>(User.class))
//      .addResource(new EnvironmentRestService(resourceMonitor))
//      .build();
//
//  @Before
//  public void setUp()
//  throws Exception {
//    authenticator = mock(TeamNotifierAuthenticator.class);
//    final Optional<User> mockUser = Optional.of(new User("name", "surname"));
//    when(authenticator.authenticate(any())).thenReturn(mockUser);
//  }
//
//  @Test
//  public void testProtected()
//  throws Exception {
//    final Response response = rule.getJerseyTest().target("/protected")
//        .request(MediaType.APPLICATION_JSON_TYPE)
//        .header("Authorization", "Bearer TOKEN")
//        .get();
//    assertThat(response.getStatus()).isEqualTo(200);
//  }
//}