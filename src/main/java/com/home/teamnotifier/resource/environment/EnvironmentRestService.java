package com.home.teamnotifier.resource.environment;

import com.home.teamnotifier.resource.auth.UserInfo;
import com.home.teamnotifier.routine.ResourceMonitor;
import io.dropwizard.auth.Auth;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("api/1.0/environment")
@Produces(MediaType.APPLICATION_JSON)
public class EnvironmentRestService {

  private final ResourceMonitor resourceMonitor;

  public EnvironmentRestService(final ResourceMonitor resourceMonitor) {
    this.resourceMonitor = resourceMonitor;
  }

  @GET
  public Envoronments getServerInfo() {
    return null;
  }

  @POST
  @Path("/application/reserve/{applicationId}")
  public void reserve(@Auth UserInfo userInfo, @PathParam("applicationId") Integer applicationId) {
  }

  @DELETE
  @Path("/application/reserve/{applicationId}")
  public void free(@Auth UserInfo userInfo, @PathParam("applicationId") Integer applicationId) {
  }

  @POST
  @Path("/server/subscribe/{serverId}")
  public void subscribe(@Auth UserInfo userInfo, @PathParam("serverId") Integer serverId) {
  }

  @DELETE
  @Path("/server/reserve/{serverId}")
  public void unsubscribe(@Auth UserInfo userInfo, @PathParam("serverId") Integer serverId) {
  }
}
