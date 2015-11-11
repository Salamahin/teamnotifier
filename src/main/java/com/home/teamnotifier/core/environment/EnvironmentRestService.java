package com.home.teamnotifier.core.environment;

import com.google.common.collect.Range;
import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;
import com.home.teamnotifier.authentication.*;
import com.home.teamnotifier.core.ResourceMonitor;
import com.home.teamnotifier.gateways.*;
import com.home.teamnotifier.utils.PasswordHasher;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.basic.BasicCredentials;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.*;
import static com.home.teamnotifier.utils.BasicAuthenticationCredentialExtractor.extract;

@Path("1.0/environment")
@Produces(MediaType.APPLICATION_JSON)
public class EnvironmentRestService {

  private final ResourceMonitor resourceMonitor;

  private final TokenCreator tokenCreator;

  private final UserGateway userGateway;

  @Inject
  public EnvironmentRestService(
      final ResourceMonitor resourceMonitor,
      final TokenCreator tokenCreator,
      final UserGateway userGateway
  ) {
    this.resourceMonitor = resourceMonitor;
    this.tokenCreator = tokenCreator;
    this.userGateway = userGateway;
  }

  @GET
  @Path("/authenticate")
  public AuthenticationInfo authenticate(
      @HeaderParam(HttpHeaders.AUTHORIZATION) final String encodedCredentials
  ) {
    final BasicCredentials credentials = extract(encodedCredentials);
    final String username = credentials.getUsername();

    final UserCredentials persistedCredentials = userGateway.userCredentials(username);

    if(compareCredentials(credentials, persistedCredentials))
      return new AuthenticationInfo(tokenCreator.getTokenFor(username));

    return null;
  }

  private boolean compareCredentials(
      final BasicCredentials provided,
      final UserCredentials persisted
  ) {
    if (persisted == null) { return false; }

    final String providedPassHash = PasswordHasher.toMd5Hash(provided.getPassword());
    return Objects.equals(providedPassHash, persisted.getPassHash());
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

  private String decodeBase64String(final String encodedString) {
    return new String(
        Base64.getDecoder().decode(encodedString),
        Charset.forName("UTF-8"));
  }

  @GET
  @Path("/application/actions/{applicationId}")
  @RolesAllowed({TeamNotifierRoles.USER})
  public ActionsInfo getActionsInfo(
      @PathParam("applicationId") final Integer applicationId,
      @HeaderParam("ActionsFrom") final String encodedBase64From,
      @HeaderParam("ActionsTo") final String encodedBase64To
  ) {
    final LocalDateTime fromTime = LocalDateTime.parse(decodeBase64String(encodedBase64From));
    final LocalDateTime toTime = LocalDateTime.parse(decodeBase64String(encodedBase64To));
    return resourceMonitor.actionsInfo(applicationId, Range.closed(fromTime, toTime));
  }

  @POST
  @Path("/application/actions/{applicationId}")
  @RolesAllowed({TeamNotifierRoles.USER})
  public void newInfo(
      @Auth final User user,
      @PathParam("applicationId") final Integer applicationId,
      @HeaderParam("ActionDetails") final String details
  ) {
    resourceMonitor.newAction(user.getName(), applicationId, details);
  }
}
