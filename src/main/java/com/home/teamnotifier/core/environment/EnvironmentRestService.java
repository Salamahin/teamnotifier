package com.home.teamnotifier.core.environment;

import com.google.common.collect.Range;
import com.home.teamnotifier.authentication.*;
import com.home.teamnotifier.core.ResourceMonitor;
import com.home.teamnotifier.gateways.UserGateway;
import io.dropwizard.auth.Auth;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;

@Path("1.0/environment")
@Produces(MediaType.APPLICATION_JSON)
public class EnvironmentRestService {

  private final ResourceMonitor resourceMonitor;
  private final UserGateway userGateway;

  public EnvironmentRestService(
      final ResourceMonitor resourceMonitor,
      final UserGateway userGateway
  ) {
    this.resourceMonitor = resourceMonitor;
    this.userGateway = userGateway;
  }

  @POST
  @Path("/users/register{name}{password}")
  public void newUser(
      @PathParam("name") final String name,
      @PathParam("password")final String password
  ) {
    userGateway.newUser(name, password);
  }

  @POST
  @Path("/application/reserve/{applicationId}")
  @RolesAllowed({TeamNotifierRoles.USER})
  public void reserve(@Auth final User user, @PathParam("applicationId") final Integer applicationId) {
    resourceMonitor.reserve(user.getName(), applicationId);
  }

  @DELETE
  @Path("/application/reserve/{applicationId}")
  @RolesAllowed({TeamNotifierRoles.USER})
  public void free(@Auth final User user, @PathParam("applicationId") final Integer applicationId) {
    resourceMonitor.free(user.getName(), applicationId);
  }

  @POST
  @Path("/server/subscribe/{serverId}")
  @RolesAllowed({TeamNotifierRoles.USER})
  public void subscribe(@Auth final User user, @PathParam("serverId") final Integer serverId) {
    resourceMonitor.subscribe(user.getName(), serverId);
  }

  @DELETE
  @Path("/server/reserve/{serverId}")
  @RolesAllowed({TeamNotifierRoles.USER})
  public void unsubscribe(@Auth final User user, @PathParam("serverId") final Integer serverId) {
    resourceMonitor.unsubscribe(user.getName(), serverId);
  }

  @GET
  @RolesAllowed({TeamNotifierRoles.USER})
  public EnvironmentsInfo getServerInfo(@Auth final User user) {
    return resourceMonitor.status();
  }

  @GET
  @Path("/application/actions/{applicationId}{from}{to}")
  @RolesAllowed({TeamNotifierRoles.USER})
  public ActionsInfo getActionsInfo(
      @PathParam("applicationId") final Integer applicationId,
      @PathParam("from") final String from,
      @PathParam("to") final String to) {

    final LocalDateTime fromTime = LocalDateTime.parse(from);
    final LocalDateTime toTime = LocalDateTime.parse(to);
    return resourceMonitor.actionsInfo(applicationId, Range.closed(fromTime, toTime));
  }
}
