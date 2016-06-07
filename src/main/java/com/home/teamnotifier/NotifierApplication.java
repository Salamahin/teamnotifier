package com.home.teamnotifier;

import be.tomcools.dropwizard.websocket.WebsocketBundle;
import com.bazaarvoice.dropwizard.assets.ConfiguredAssetsBundle;
import com.google.inject.Injector;
import com.home.teamnotifier.health.DbConnection;
import com.home.teamnotifier.health.ServerStates;
import com.home.teamnotifier.health.Sessions;
import com.home.teamnotifier.web.lifecycle.ExecutorsManager;
import com.home.teamnotifier.web.lifecycle.ServerStatusCheckerManager;
import com.home.teamnotifier.web.lifecycle.TransactionManager;
import com.home.teamnotifier.web.rest.EnvironmentRestService;
import com.home.teamnotifier.web.rest.UserRestService;
import com.home.teamnotifier.web.socket.NotificationEndpoint;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.websocket.jsr356.server.BasicServerEndpointConfigurator;
import ru.vyarus.dropwizard.guice.GuiceBundle;
import ru.vyarus.dropwizard.guice.module.installer.feature.ManagedInstaller;
import ru.vyarus.dropwizard.guice.module.installer.feature.health.HealthCheckInstaller;
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.JerseyFeatureInstaller;
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.ResourceInstaller;
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.provider.JerseyProviderInstaller;

import javax.websocket.server.ServerEndpointConfig;

public class NotifierApplication extends Application<NotifierConfiguration> {

    private final WebsocketBundle<NotifierConfiguration> websocketBundle = new WebsocketBundle<>();
    private GuiceBundle<NotifierConfiguration> guiceBundle;

    public static void main(String[] args) throws Exception {
        new NotifierApplication().run(args);
    }

    @Override
    public void initialize(final Bootstrap<NotifierConfiguration> bootstrap) {
        bootstrap.addBundle(new ConfiguredAssetsBundle("/src/main/assets", "/"));
        bootstrap.addBundle(websocketBundle);

        guiceBundle = GuiceBundle.<NotifierConfiguration>builder()
                .installers(
                        HealthCheckInstaller.class,
                        ManagedInstaller.class,
                        JerseyFeatureInstaller.class,
                        JerseyProviderInstaller.class,
                        ResourceInstaller.class
                )
                .modules(new NotifierModule())
                .extensions(DbConnection.class, Sessions.class, ServerStates.class) //healthcheck
                .extensions(ExecutorsManager.class, TransactionManager.class, ServerStatusCheckerManager.class) //lifecycle
                .extensions(AuthenticationDynamicFeature.class) //features
                .extensions(EnvironmentRestService.class, UserRestService.class)
                .build();
        bootstrap.addBundle(guiceBundle);

    }

    Injector getInjector() {
        return guiceBundle.getInjector();
    }

    @Override
    public void run(final NotifierConfiguration configuration, final Environment environment) {
        ServerEndpointConfig serverEndpointConfig = ServerEndpointConfig.Builder
                .create(NotificationEndpoint.class, "/teamnotifier/1.0/state/{token}")
                .configurator(new BasicServerEndpointConfigurator() {
                    @Override
                    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
                        return getInjector().getInstance(endpointClass);
                    }
                })
                .build();

        websocketBundle.addEndpoint(serverEndpointConfig);
    }

}
