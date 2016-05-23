package com.home.teamnotifier.web.rest;

import com.google.common.collect.Range;
import com.google.inject.Inject;
import com.home.teamnotifier.authentication.BasicAuthenticated;
import com.home.teamnotifier.authentication.TokenAuthenticated;
import com.home.teamnotifier.core.ResourceMonitor;
import com.home.teamnotifier.core.responses.action.ResourceActionsHistory;
import com.home.teamnotifier.core.responses.action.ServerActionsHistory;
import com.home.teamnotifier.core.responses.action.ServerSubscribersInfo;
import com.home.teamnotifier.core.responses.status.EnvironmentsInfo;
import com.home.teamnotifier.gateways.ResourceDescription;
import io.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Base64;

@Path("1.0/environment")
@Produces(MediaType.APPLICATION_JSON)
public class EnvironmentRestService {
    private final Logger LOGGER = LoggerFactory.getLogger(EnvironmentRestService.class);

    private final ResourceMonitor resourceMonitor;

    @Inject
    public EnvironmentRestService(final ResourceMonitor resourceMonitor) {
        this.resourceMonitor = resourceMonitor;
    }

    @POST
    @Path("/application/{applicationId}/reserve")
    public void reserve(
            @Auth final TokenAuthenticated userPrincipal,
            @PathParam("applicationId") final Integer applicationId
    ) {
        final String userName = userPrincipal.getName();
        LOGGER.info("User {} reserve resource id {} request", userName, applicationId);
        resourceMonitor.reserve(userName, applicationId);
    }

    @DELETE
    @Path("/application/{applicationId}/reserve")
    public void free(
            @Auth final TokenAuthenticated userPrincipal,
            @PathParam("applicationId") final Integer applicationId
    ) {
        final String userName = userPrincipal.getName();
        LOGGER.info("User {} free resource id {} request", userName, applicationId);
        resourceMonitor.free(userName, applicationId);
    }

    @POST
    @Path("/server/{serverId}/subscribe")
    public ServerSubscribersInfo subscribe(
            @Auth final TokenAuthenticated userPrincipal,
            @PathParam("serverId") final Integer serverId
    ) {
        final String userName = userPrincipal.getName();
        LOGGER.info("User {} subscribe on server id {} request", userName, serverId);
        return resourceMonitor.subscribe(userName, serverId);
    }

    @DELETE
    @Path("/server/{serverId}/subscribe")
    public void unsubscribe(
            @Auth final TokenAuthenticated userPrincipal,
            @PathParam("serverId") final Integer serverId) {
        final String userName = userPrincipal.getName();
        LOGGER.info("User {} unsubscribe from server id {} request", userName, serverId);
        resourceMonitor.unsubscribe(userName, serverId);
    }

    @POST
    @Path("/application/{applicationId}/action")
    public void newResourceAction(
            @Auth final TokenAuthenticated userPrincipal,
            @PathParam("applicationId") final Integer applicationId,
            @QueryParam("details") final String details
    ) {
        final String userName = userPrincipal.getName();
        LOGGER.info("User {} new action on resource id {} ({}) request", userName, applicationId, details);
        resourceMonitor.newResourceAction(userName, applicationId, details);
    }

    @POST
    @Path("/server/{serverId}/action")
    public void newServerAction(
            @Auth final TokenAuthenticated userPrincipal,
            @PathParam("serverId") final Integer serverId,
            @QueryParam("details") final String details
    ) {
        final String userName = userPrincipal.getName();
        LOGGER.info("User {} new action on server id {} ({}) request", userName, serverId, details);
        resourceMonitor.newServerAction(userName, serverId, details);
    }

    @POST
    @Path("/application/action/")
    public void newResourceAction(
            @Auth final BasicAuthenticated userPrincipal,
            @QueryParam("environment") final String environmentName,
            @QueryParam("server") final String serverName,
            @QueryParam("application") final String resourceName,
            @QueryParam("details") final String details
    ) {
        final String userName = userPrincipal.getName();
        LOGGER.info("User {} new action on resource {} {} ({}) request", userName, serverName, resourceName, details);

        final ResourceDescription resourceDescription = ResourceDescription.newBuilder()
                .withResourceName(resourceName)
                .withServerName(serverName)
                .withEnvironmentName(environmentName)
                .build();

        resourceMonitor.newResourceAction(userName, resourceDescription, details);
    }

    @GET
    public EnvironmentsInfo getServerInfo(@Auth final TokenAuthenticated userPrincipal) {
        LOGGER.info("User {} status request", userPrincipal.getName());
        return resourceMonitor.status();
    }

    @GET
    @Path("/application/{applicationId}/action")
    public ResourceActionsHistory getResourceActionsHistory(
            @Auth final TokenAuthenticated userPrincipal,
            @PathParam("applicationId") final Integer applicationId,
            @QueryParam("from") final String fromISO8601,
            @QueryParam("to") final String toISO8601
    ) {
        final Instant fromInstant = ZonedDateTime.parse(fromISO8601).toInstant();
        final Instant toInstant = ZonedDateTime.parse(toISO8601).toInstant();

        LOGGER.info("User {} actions on resource {} from {} to {} request",
                userPrincipal.getName(),
                applicationId,
                fromInstant,
                toInstant
        );

        return resourceMonitor.resourceActions(applicationId, Range.closed(fromInstant, toInstant));
    }

    @GET
    @Path("/server/{serverId}/action")
    public ServerActionsHistory getServerActionsHistory(
            @Auth final TokenAuthenticated userPrincipal,
            @PathParam("serverId") final Integer serverId,
            @QueryParam("from") final String fromISO8601,
            @QueryParam("to") final String toISO8601
    ) {
        final Instant fromInstant = ZonedDateTime.parse(fromISO8601).toInstant();
        final Instant toInstant = ZonedDateTime.parse(toISO8601).toInstant();

        LOGGER.info("User {} actions on resource {} from {} to {} request",
                userPrincipal.getName(),
                serverId,
                fromInstant,
                toInstant
        );

        return resourceMonitor.serverActions(serverId, Range.closed(fromInstant, toInstant));
    }
}
