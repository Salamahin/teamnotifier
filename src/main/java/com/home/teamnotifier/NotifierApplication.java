package com.home.teamnotifier;

import com.github.toastshaman.dropwizard.auth.jwt.JWTAuthFilter;
import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenParser;
import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA512Verifier;
import com.github.toastshaman.dropwizard.auth.jwt.parser.DefaultJsonWebTokenParser;
import com.home.teamnotifier.authentication.*;
import com.home.teamnotifier.core.AppServerAvailabilityChecker;
import com.home.teamnotifier.gateways.EnvironmentGateway;
import com.home.teamnotifier.gateways.UserGateway;
import com.home.teamnotifier.health.AppServerStates;
import com.home.teamnotifier.health.DbConnection;
import com.home.teamnotifier.health.Sessions;
import com.home.teamnotifier.web.lifecycle.ExecutorsManager;
import com.home.teamnotifier.web.lifecycle.ServerStatusCheckerManager;
import com.home.teamnotifier.web.lifecycle.TransactionManager;
import com.home.teamnotifier.web.socket.BroadcastServlet;
import com.home.teamnotifier.web.socket.ClientManager;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.PermitAllAuthorizer;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
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

        final WebsocketAuthenticator websocketAuthenticator = registerJwtAuthenticator(configuration, environment);
        registerWebsocket(environment, websocketAuthenticator, clientManager);
        addHealthcheks(environment, clientManager);

        addLifecycleHooks(environment);
    }

    private void addLifecycleHooks(final Environment environment) {
        environment.lifecycle().manage(INJECTION_BUNDLE.getInjector().getInstance(ExecutorsManager.class));
        environment.lifecycle().manage(INJECTION_BUNDLE.getInjector().getInstance(TransactionManager.class));
        environment.lifecycle().manage(INJECTION_BUNDLE.getInjector().getInstance(ServerStatusCheckerManager.class));
    }

    private void addHealthcheks(final Environment environment, final ClientManager clientManager) {
        environment.healthChecks().register("DbConnection", new DbConnection(
                INJECTION_BUNDLE.getInjector().getInstance(EnvironmentGateway.class))
        );
        environment.healthChecks().register("Sessions", new Sessions(clientManager));
        environment.healthChecks().register("ServerStatuses", new AppServerStates(
                INJECTION_BUNDLE.getInjector().getInstance(AppServerAvailabilityChecker.class))
        );
    }

    private WebsocketAuthenticator registerJwtAuthenticator(
            NotifierConfiguration configuration, 
            Environment environment
    ) {
        final byte[] jwtSecret = configuration.getAuthenticationConfiguration().getJwtSecret().getBytes();
        final JsonWebTokenParser tokenParser = new DefaultJsonWebTokenParser();
        final HmacSHA512Verifier tokenVerifier = new HmacSHA512Verifier(jwtSecret);
        
        final JwtTokenAuthenticator jwt = new JwtTokenAuthenticator(
                INJECTION_BUNDLE.getInjector().getInstance(UserGateway.class),
               tokenVerifier
        );

        environment.jersey().register(new AuthDynamicFeature(
                new BasicCredentialAuthFilter.Builder<BasicPrincipal>()
                        .setAuthenticator(new BasicAuthenticator())
                        .setAuthorizer(new ExampleAuthorizer())
                        .setRealm("SUPER SECRET STUFF")
                        .buildAuthFilter()));

        environment.jersey().register(new AuthDynamicFeature(
                        new JWTAuthFilter.Builder<OathPrincipal>()
                                .setTokenParser(tokenParser)
                                .setTokenVerifier(tokenVerifier)
                                .setPrefix("Bearer")
                                .setAuthenticator(jwt)
                                .setAuthorizer(new PermitAllAuthorizer<>())
                                .buildAuthFilter()
                )
        );
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(OathPrincipal.class));
        
        return jwt;
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
