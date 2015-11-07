package com.home.teamnotifier;

import com.home.teamnotifier.authentication.*;
import com.home.teamnotifier.db.TransactionHelper;
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
  public void run(NotifierConfiguration configuration, Environment environment) {
    registerWebsocket(environment);
    final AuthDynamicFeature authDynamicFeature = new AuthDynamicFeature(
        new BasicCredentialAuthFilter.Builder<User>()
            .setAuthenticator(
                INJECTION_BUNDLE.getInjector().getInstance(TeamNotifierAuthenticator.class))
            .setAuthorizer(new TrivialAuthorizer())
            .buildAuthFilter()
    );

    environment.jersey().register(authDynamicFeature);
    environment.jersey().register(RolesAllowedDynamicFeature.class);
    environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
    environment.healthChecks().register(
        "DbConnection",
        new DbConnection(INJECTION_BUNDLE.getInjector().getInstance(EnvironmentGateway.class))
    );
  }

  private void registerWebsocket(final Environment environment) {
    final ClientManager clientManager = INJECTION_BUNDLE
        .getInjector()
        .getInstance(ClientManager.class);

    final ServletRegistration.Dynamic websocket = environment
        .servlets()
        .addServlet("ws", new BroadcastServlet(clientManager));

    websocket.setAsyncSupported(true);
    websocket.addMapping("/ws/*");
  }

  public static void main(String[] args)
  throws Exception {
    new NotifierApplication().run(args);
  }
}
