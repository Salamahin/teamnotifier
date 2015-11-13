package com.home.teamnotifier;

import com.github.toastshaman.dropwizard.auth.jwt.*;
import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA512Verifier;
import com.github.toastshaman.dropwizard.auth.jwt.parser.DefaultJsonWebTokenParser;
import com.home.teamnotifier.authentication.*;
import com.home.teamnotifier.gateways.EnvironmentGateway;
import com.home.teamnotifier.health.DbConnection;
import com.home.teamnotifier.web.socket.*;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.*;
import io.dropwizard.setup.*;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import javax.servlet.ServletRegistration;
import static com.home.teamnotifier.Injection.INJECTION_BUNDLE;

public class NotifierApplication extends Application<NotifierConfiguration> {

  @Override
  public void initialize(final Bootstrap<NotifierConfiguration> bootstrap) {
    bootstrap.addBundle(new AssetsBundle("/assets", "/"));
    bootstrap.addBundle(INJECTION_BUNDLE);
  }

  @Override
  public void run(final NotifierConfiguration configuration, final Environment environment) {
    final JwtTokenAuthenticator authenticator = INJECTION_BUNDLE
        .getInjector()
        .getInstance(JwtTokenAuthenticator.class);
    final ClientManager clientManager = INJECTION_BUNDLE
        .getInjector()
        .getInstance(ClientManager.class);

    registerWebsocket(environment, authenticator, clientManager);

    final JsonWebTokenParser tokenParser = new DefaultJsonWebTokenParser();
    final HmacSHA512Verifier tokenVerifier = new HmacSHA512Verifier(configuration.getJwtTokenSecret());

    environment.jersey().register(new AuthDynamicFeature(
            new JWTAuthFilter.Builder<AuthenticatedUserData>()
                .setTokenParser(tokenParser)
                .setTokenVerifier(tokenVerifier)
                .setAuthenticator(authenticator)
                .setAuthorizer(new PermitAllAuthorizer<>())
                .buildAuthFilter()
        )
    );
    environment.jersey().register(RolesAllowedDynamicFeature.class);
    environment.jersey().register(new AuthValueFactoryProvider.Binder<>(AuthenticatedUserData.class));

    final EnvironmentGateway environmentGateway = INJECTION_BUNDLE
        .getInjector()
        .getInstance(EnvironmentGateway.class);

    environment.healthChecks().register("DbConnection", new DbConnection(environmentGateway));
  }

  private void registerWebsocket(
      final Environment environment,
      final WebsocketAuthenticator authenticator,
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
