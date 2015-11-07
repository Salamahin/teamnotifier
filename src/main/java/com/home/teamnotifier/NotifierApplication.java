package com.home.teamnotifier;

import com.home.teamnotifier.authentication.*;
import com.home.teamnotifier.gateways.EnvironmentGateway;
import com.home.teamnotifier.health.DbConnection;
import com.home.teamnotifier.web.socket.*;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.*;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.*;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import javax.servlet.ServletRegistration;
import static com.home.teamnotifier.Injection.INJECTION_BUNDLE;

public class NotifierApplication extends Application<NotifierConfiguration> {

  @Override
  public void initialize(Bootstrap<NotifierConfiguration> bootstrap) {
    bootstrap.addBundle(new AssetsBundle("/assets", "/"));
    bootstrap.addBundle(INJECTION_BUNDLE);
  }

  @Override
  public void run(final NotifierConfiguration configuration, final Environment environment) {
    final TeamNotifierAuthenticator authenticator = INJECTION_BUNDLE
        .getInjector()
        .getInstance(TeamNotifierAuthenticator.class);
    final ClientManager clientManager = INJECTION_BUNDLE
        .getInjector()
        .getInstance(ClientManager.class);

    registerWebsocket(environment, authenticator, clientManager);

    final AuthDynamicFeature authDynamicFeature = new AuthDynamicFeature(
        new BasicCredentialAuthFilter.Builder<User>()
            .setAuthenticator(
                authenticator)
            .setAuthorizer(new TrivialAuthorizer())
            .buildAuthFilter()
    );

    environment.jersey().register(authDynamicFeature);
    environment.jersey().register(RolesAllowedDynamicFeature.class);
    environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));

    final EnvironmentGateway environmentGateway = INJECTION_BUNDLE
        .getInjector()
        .getInstance(EnvironmentGateway.class);
    environment.healthChecks().register("DbConnection", new DbConnection(environmentGateway));
  }

  private void registerWebsocket(
      final Environment environment,
      final TeamNotifierAuthenticator authenticator,
      final ClientManager manager
  ) {

    final ServletRegistration.Dynamic websocket = environment
        .servlets()
        .addServlet("broadcastServlet", new BroadcastServlet(manager, authenticator));

    websocket.setAsyncSupported(true);
    websocket.addMapping("/state/*");
  }

  public static void main(String[] args)
  throws Exception {
    new NotifierApplication().run(args);
  }
}
