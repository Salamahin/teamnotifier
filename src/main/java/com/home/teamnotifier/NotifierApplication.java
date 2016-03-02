package com.home.teamnotifier;

import com.github.toastshaman.dropwizard.auth.jwt.JWTAuthFilter;
import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenParser;
import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenVerifier;
import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.home.teamnotifier.authentication.*;
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
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.PermitAllAuthorizer;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.chained.ChainedAuthFilter;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.servlet.ServletRegistration;

import java.util.List;

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
        resisterAuthenticators(environment);

        registerWebsocket(environment);

        addHealthcheks(environment);
        addLifecycleHooks(environment);
    }

    private void addLifecycleHooks(final Environment environment) {
        environment.lifecycle().manage(INJECTION_BUNDLE.getInjector().getInstance(ExecutorsManager.class));
        environment.lifecycle().manage(INJECTION_BUNDLE.getInjector().getInstance(TransactionManager.class));
        environment.lifecycle().manage(INJECTION_BUNDLE.getInjector().getInstance(ServerStatusCheckerManager.class));
    }

    private void addHealthcheks(final Environment environment) {
        final Injector injector = INJECTION_BUNDLE.getInjector();

        environment.healthChecks().register("DbConnection", injector.getInstance(DbConnection.class));
        environment.healthChecks().register("Sessions", injector.getInstance(Sessions.class));
        environment.healthChecks().register("ServerStatuses", injector.getInstance(AppServerStates.class));
    }

    private void resisterAuthenticators(final Environment environment) {
        final Injector injector = INJECTION_BUNDLE.getInjector();
        final JsonWebTokenParser tokenParser = injector.getInstance(JsonWebTokenParser.class);
        final JsonWebTokenVerifier tokenVerifier = injector.getInstance(JsonWebTokenVerifier.class);
        final JwtTokenAuthenticator jwtTokenAuthenticator = injector.getInstance(JwtTokenAuthenticator.class);
        final BasicAuthenticator basicAuthenticator = injector.getInstance(BasicAuthenticator.class);
        final UserAuthorizer userAuthorizer = new UserAuthorizer();

        final JWTAuthFilter<UserPrincipal> jwt = new JWTAuthFilter.Builder<UserPrincipal>()
                .setTokenParser(tokenParser)
                .setTokenVerifier(tokenVerifier)
                .setPrefix("Bearer")
                .setAuthenticator(jwtTokenAuthenticator)
                .setAuthorizer(userAuthorizer)
                .buildAuthFilter();

        final BasicCredentialAuthFilter<UserPrincipal> simple = new BasicCredentialAuthFilter.Builder<UserPrincipal>()
                .setAuthenticator(basicAuthenticator)
                .setAuthorizer(userAuthorizer)
                .buildAuthFilter();

        final List<AuthFilter<?, ?>> filters = Lists.newArrayList(jwt, simple);

        environment.jersey().register(new AuthDynamicFeature(new ChainedAuthFilter(filters)));

        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(UserPrincipal.class));

        environment.jersey().register(RolesAllowedDynamicFeature.class);
    }

    private void registerWebsocket(final Environment environment) {

        final Injector injector = INJECTION_BUNDLE.getInjector();

        final WebsocketAuthenticator websocketAuthenticator = injector.getInstance(WebsocketAuthenticator.class);
        final ClientManager clientManager = injector.getInstance(ClientManager.class);


        final ServletRegistration.Dynamic websocket = environment
                .servlets()
                .addServlet("broadcastServlet", new BroadcastServlet(clientManager, websocketAuthenticator));

        websocket.setAsyncSupported(true);
        websocket.addMapping("/state/*");
    }
}
