package com.home.teamnotifier.core.environment;

import com.google.common.collect.Range;
import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;
import com.home.teamnotifier.authentication.*;
import com.home.teamnotifier.core.ResourceMonitor;
import com.home.teamnotifier.gateways.UserGateway;
import com.home.teamnotifier.utils.BasicAuthenticationCredentialExtractor;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.basic.BasicCredentials;
import org.slf4j.*;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.LocalDateTime;
import static com.home.teamnotifier.utils.BasicAuthenticationCredentialExtractor.*;

@Path("1.0/environment")
@Produces(MediaType.APPLICATION_JSON)
public class EnvironmentRestService {

  private final ResourceMonitor resourceMonitor;

  private final UserGateway userGateway;

  @Inject
  public EnvironmentRestService(
      final ResourceMonitor resourceMonitor,
      final UserGateway userGateway
  ) {
    this.resourceMonitor = resourceMonitor;
    this.userGateway = userGateway;
  }

  @POST
  @Path("/users/register")
  public void newUser(@HeaderParam(HttpHeaders.AUTHORIZATION) final String encodedCredentials) {
    final BasicCredentials credentials = extract(encodedCredentials);
    userGateway.newUser(credentials.getUsername(), credentials.getPassword());
  }

  @POST
  @Path("/application/reserve/{applicationId}")
  @RolesAllowed({TeamNotifierRoles.USER})
  public void reserve(
      @Auth final User user,
      @PathParam("applicationId") final Integer applicationId
  ) {
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
  @Path("/server/subscribe/{serverId}")
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
  @Path("/application/actions/{applicationId}")
  @RolesAllowed({TeamNotifierRoles.USER})
  public ActionsInfo getActionsInfo(
      @PathParam("applicationId") final Integer applicationId,
      @QueryParam("from") final String from,
      @QueryParam("to") final String to
  ) {
    final LocalDateTime fromTime = LocalDateTime.parse(from);
    final LocalDateTime toTime = LocalDateTime.parse(to);
    return resourceMonitor.actionsInfo(applicationId, Range.closed(fromTime, toTime));
  }
}
