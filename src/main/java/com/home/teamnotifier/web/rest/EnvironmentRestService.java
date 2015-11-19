package com.home.teamnotifier.web.rest;

import com.google.common.collect.Range;
import com.google.inject.Inject;
import com.home.teamnotifier.authentication.AuthenticatedUserData;
import com.home.teamnotifier.core.ResourceMonitor;
import com.home.teamnotifier.core.responses.action.ActionsInfo;
import com.home.teamnotifier.core.responses.status.EnvironmentsInfo;
import io.dropwizard.auth.Auth;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Base64;

@Path("1.0/environment")
@Produces(MediaType.APPLICATION_JSON)
public class EnvironmentRestService {

  private final ResourceMonitor resourceMonitor;

  @Inject
  public EnvironmentRestService(final ResourceMonitor resourceMonitor) {
    this.resourceMonitor = resourceMonitor;
  }

  @POST
  @Path("/application/reserve/{applicationId}")
  public void reserve(
      @Auth final AuthenticatedUserData authenticatedUserData,
      @PathParam("applicationId") final Integer applicationId
  ) {
    resourceMonitor.reserve(authenticatedUserData.getName(), applicationId);
  }

  @DELETE
  @Path("/application/reserve/{applicationId}")
  public void free(@Auth final AuthenticatedUserData authenticatedUserData,
      @PathParam("applicationId") final Integer applicationId) {
    resourceMonitor.free(authenticatedUserData.getName(), applicationId);
  }

  @POST
  @Path("/server/subscribe/{serverId}")
  public void subscribe(@Auth final AuthenticatedUserData authenticatedUserData,
      @PathParam("serverId") final Integer serverId) {
    resourceMonitor.subscribe(authenticatedUserData.getName(), serverId);
  }

  @DELETE
  @Path("/server/subscribe/{serverId}")
  public void unsubscribe(@Auth final AuthenticatedUserData authenticatedUserData,
      @PathParam("serverId") final Integer serverId) {
    resourceMonitor.unsubscribe(authenticatedUserData.getName(), serverId);
  }

  @POST
  @Path("/application/action/{applicationId}")
  public void newInfo(
      @Auth final AuthenticatedUserData authenticatedUserData,
      @PathParam("applicationId") final Integer applicationId,
      @HeaderParam("ActionDetails") final String details
  ) {
    resourceMonitor.newAction(authenticatedUserData.getName(), applicationId, details);
  }

  @GET
  public EnvironmentsInfo getServerInfo(@Auth final AuthenticatedUserData authenticatedUserData) {
    return resourceMonitor.status();
  }

  @GET
  @Path("/application/action/{applicationId}")
  public ActionsInfo getActionsInfo(
      @PathParam("applicationId") final Integer applicationId,
      @HeaderParam("ActionsFrom") final String encodedBase64From,
      @HeaderParam("ActionsTo") final String encodedBase64To
  ) {
    final LocalDateTime fromTime = LocalDateTime.parse(decodeBase64String(encodedBase64From));
    final LocalDateTime toTime = LocalDateTime.parse(decodeBase64String(encodedBase64To));
    return resourceMonitor.actionsInfo(applicationId, Range.closed(fromTime, toTime));
  }

  private String decodeBase64String(final String encodedString) {
    return new String(
        Base64.getDecoder().decode(encodedString),
        Charset.forName("UTF-8"));
  }
}
