package com.home.teamnotifier;

import com.hubspot.dropwizard.guice.GuiceBundle;
import com.home.teamnotifier.web.socket.BroadcastServlet;
import com.home.teamnotifier.web.socket.ClientManager;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.validation.valuehandling.OptionalValidatedValueUnwrapper;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import javax.servlet.ServletRegistration;

public class NotifierApplication extends Application<NotifierConfiguration>
{

  private GuiceBundle<NotifierConfiguration> applicationInjector;

  @Override
  public void initialize(Bootstrap<NotifierConfiguration> bootstrap)
  {
    bootstrap.addBundle(new AssetsBundle("/assets", "/"));
    applicationInjector=GuiceBundle.<NotifierConfiguration>newBuilder()
        .addModule(new NotifierModule())
        .build();
    bootstrap.addBundle(applicationInjector);
  }

  @Override
  public void run(NotifierConfiguration configuration, Environment environment)
  {
    registerWebsocket(environment);
    //    environment.jersey().packages(VideoRestService.class.getName());
    environment.jersey().enable(RolesAllowedDynamicFeature.class.getName());
    environment.jersey().register(OptionalValidatedValueUnwrapper.class);
    //    environment.healthChecks().register("webcam", new WebcamHealthCheck());
  }

  private void registerWebsocket(final Environment environment)
  {
    final ClientManager clientManager=applicationInjector.getInjector()
        .getInstance(ClientManager.class);
    final ServletRegistration.Dynamic websocket=environment.servlets()
        .addServlet("ws", new BroadcastServlet(clientManager));
    websocket.setAsyncSupported(true);
    websocket.addMapping("/ws/*");
  }

  public static void main(String[] args) throws Exception
  {
    new NotifierApplication().run(args);
  }
}
