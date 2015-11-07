package com.home.teamnotifier.core.environment;

import com.home.teamnotifier.authentication.*;
import com.home.teamnotifier.core.ResourceMonitor;
import io.dropwizard.auth.Auth;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("1.0/environment")
@Produces(MediaType.APPLICATION_JSON)
public class EnvironmentRestService {

  private final ResourceMonitor resourceMonitor;

  public EnvironmentRestService(final ResourceMonitor resourceMonitor) {
    this.resourceMonitor = resourceMonitor;
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

  @GET
  @RolesAllowed({TeamNotifierRoles.USER})
  public EnvironmentsInfo getServerInfo(@Auth User user) {
    return resourceMonitor.status();
  }
}
