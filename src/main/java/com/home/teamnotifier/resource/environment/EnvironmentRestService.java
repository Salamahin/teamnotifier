package com.home.teamnotifier.resource.environment;

import com.home.teamnotifier.authentication.TeamNotifierRoles;
import com.home.teamnotifier.authentication.User;
import com.home.teamnotifier.routine.ResourceMonitor;
import io.dropwizard.auth.Auth;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("1.0/environment")
@Produces(MediaType.APPLICATION_JSON)
public class EnvironmentRestService {

  private final ResourceMonitor resourceMonitor;

  public EnvironmentRestService(final ResourceMonitor resourceMonitor) {
    this.resourceMonitor = resourceMonitor;
  }

  @GET
  @RolesAllowed({TeamNotifierRoles.USER})
  public Environments getServerInfo(@Auth User user) {
    final List<Environment> status = resourceMonitor.getStatus(user.getId());
    return new Environments(status);
  }

  @POST
  @Path("/application/reserve/{applicationId}")
  public void reserve(@PathParam("applicationId") Integer applicationId) {
  }

  @DELETE
  @Path("/application/reserve/{applicationId}")
  public void free(@PathParam("applicationId") Integer applicationId) {
  }

  @POST
  @Path("/server/subscribe/{serverId}")
  public void subscribe(@PathParam("serverId") Integer serverId) {
  }

  @DELETE
  @Path("/server/reserve/{serverId}")
  public void unsubscribe(@PathParam("serverId") Integer serverId) {
  }
}
