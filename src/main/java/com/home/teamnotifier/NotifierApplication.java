package com.home.teamnotifier;

import com.github.toastshaman.dropwizard.auth.jwt.JWTAuthFilter;
import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenParser;
import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA512Verifier;
import com.github.toastshaman.dropwizard.auth.jwt.parser.DefaultJsonWebTokenParser;
import com.home.teamnotifier.authentication.AuthenticatedUserData;
import com.home.teamnotifier.authentication.JwtTokenAuthenticator;
import com.home.teamnotifier.authentication.WebsocketAuthenticator;
import com.home.teamnotifier.gateways.EnvironmentGateway;
import com.home.teamnotifier.gateways.UserGateway;
import com.home.teamnotifier.health.DbConnection;
import com.home.teamnotifier.health.Sessions;
import com.home.teamnotifier.web.socket.BroadcastServlet;
import com.home.teamnotifier.web.socket.ClientManager;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.PermitAllAuthorizer;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.servlet.ServletRegistration;

import static com.home.teamnotifier.Injection.INJECTION_BUNDLE;

public class NotifierApplication extends Application<NotifierConfiguration> {

    public static void main(String[] args)
            throws Exception {
        new NotifierApplication().run(args);
    }

    @Override
    public void initialize(final Bootstrap<NotifierConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets", "/"));
        bootstrap.addBundle(INJECTION_BUNDLE);
    }

    @Override
    public void run(final NotifierConfiguration configuration, final Environment environment) {
        final ClientManager clientManager = INJECTION_BUNDLE
                .getInjector()
                .getInstance(ClientManager.class);

        final JwtTokenAuthenticator authenticator = new JwtTokenAuthenticator(
                INJECTION_BUNDLE.getInjector().getInstance(UserGateway.class),
                new HmacSHA512Verifier(configuration.getJwtTokenSecret())
        );

        registerWebsocket(environment, authenticator, clientManager);

        final JsonWebTokenParser tokenParser = new DefaultJsonWebTokenParser();
        final HmacSHA512Verifier tokenVerifier = new HmacSHA512Verifier(
                configuration.getJwtTokenSecret());

        environment.jersey().register(new AuthDynamicFeature(
                        new JWTAuthFilter.Builder<AuthenticatedUserData>()
                                .setTokenParser(tokenParser)
                                .setTokenVerifier(tokenVerifier)
                                .setPrefix("Bearer")
                                .setAuthenticator(authenticator)
                                .setAuthorizer(new PermitAllAuthorizer<>())
                                .buildAuthFilter()
                )
        );
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey()
                .register(new AuthValueFactoryProvider.Binder<>(AuthenticatedUserData.class));

        final EnvironmentGateway environmentGateway = INJECTION_BUNDLE
                .getInjector()
                .getInstance(EnvironmentGateway.class);

        environment.healthChecks().register("DbConnection", new DbConnection(environmentGateway));
        environment.healthChecks().register("Sessions", new Sessions(clientManager));
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
}
