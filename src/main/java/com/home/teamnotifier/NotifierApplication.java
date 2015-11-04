package com.home.teamnotifier;

import com.google.inject.Injector;
import com.home.teamnotifier.authentication.*;
import com.home.teamnotifier.web.socket.*;
import io.dropwizard.Application;
import io.dropwizard.auth.*;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.*;
import io.dropwizard.validation.valuehandling.OptionalValidatedValueUnwrapper;
import javax.servlet.ServletRegistration;

public class NotifierApplication extends Application<NotifierConfiguration> {

  private Injector applicationInjector;

  @Override
  public void initialize(Bootstrap<NotifierConfiguration> bootstrap) {
    bootstrap.addBundle(Bundles.ASSETS);
    bootstrap.addBundle(Bundles.GUICE);
    applicationInjector = Bundles.GUICE.getInjector();
  }

  @Override
  public void run(NotifierConfiguration configuration, Environment environment) {
    registerWebsocket(environment);
    environment.jersey().register(OptionalValidatedValueUnwrapper.class);
    environment.jersey().register(new AuthDynamicFeature(
        new BasicCredentialAuthFilter.Builder<User>()
            .setAuthenticator(applicationInjector.getInstance(TeamNotifierAuthenticator.class))
            .setRealm("SUPER SECRET STUFF")
            .buildAuthFilter()));
    //If you want to use @Auth to inject a custom Principal type into your resource
    environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
  }

  private void registerWebsocket(final Environment environment) {
    final ClientManager clientManager = applicationInjector
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
