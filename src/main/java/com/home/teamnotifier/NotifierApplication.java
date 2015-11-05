package com.home.teamnotifier;

import com.google.inject.*;
import com.home.teamnotifier.authentication.*;
import com.home.teamnotifier.web.socket.*;
import com.hubspot.dropwizard.guice.GuiceBundle;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.*;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.*;
import io.dropwizard.validation.valuehandling.OptionalValidatedValueUnwrapper;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import javax.servlet.ServletRegistration;

public class NotifierApplication extends Application<NotifierConfiguration> {
  private GuiceBundle<NotifierConfiguration> applicationInjector;

  @Override
  public void initialize(Bootstrap<NotifierConfiguration> bootstrap) {
    bootstrap.addBundle(new AssetsBundle("/assets", "/"));
    applicationInjector = GuiceBundle.<NotifierConfiguration>newBuilder()
        .addModule(new NotifierModule())
        .setConfigClass(NotifierConfiguration.class)
        .enableAutoConfig(getClass().getPackage().getName())
        .setInjectorFactory((stage, modules) -> Guice.createInjector(Stage.DEVELOPMENT, modules))
        .build();
    bootstrap.addBundle(applicationInjector);
  }

  @Override
  public void run(NotifierConfiguration configuration, Environment environment) {
    registerWebsocket(environment);
    environment.jersey().register(OptionalValidatedValueUnwrapper.class);
    final Injector injector = applicationInjector.getInjector();
    environment.jersey().register(new AuthDynamicFeature(
        new BasicCredentialAuthFilter.Builder<User>()
            .setAuthenticator(injector.getInstance(TeamNotifierAuthenticator.class))
            .setAuthorizer(new TrivialAuthorizer())
            .buildAuthFilter()));
    environment.jersey().register(RolesAllowedDynamicFeature.class);
    //If you want to use @Auth to inject a custom Principal type into your resource
    environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
  }

  private void registerWebsocket(final Environment environment) {
    final ClientManager clientManager = applicationInjector.getInjector()
        .getInstance(ClientManager.class);
    final ServletRegistration.Dynamic websocket = environment.servlets()
        .addServlet("ws", new BroadcastServlet(clientManager));
    websocket.setAsyncSupported(true);
    websocket.addMapping("/ws/*");
  }

  public static void main(String[] args)
  throws Exception {
    new NotifierApplication().run(args);
  }
}
