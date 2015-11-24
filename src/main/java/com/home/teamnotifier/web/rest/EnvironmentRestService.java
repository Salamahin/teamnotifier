package com.home.teamnotifier.web.rest;

import com.google.common.collect.Range;
import com.google.inject.Inject;
import com.home.teamnotifier.authentication.AuthenticatedUserData;
import com.home.teamnotifier.core.ResourceMonitor;
import com.home.teamnotifier.core.responses.action.ActionsInfo;
import com.home.teamnotifier.core.responses.status.EnvironmentsInfo;
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
    @Path("/application/reserve/{applicationId}")
    public void reserve(
            @Auth final AuthenticatedUserData authenticatedUserData,
            @PathParam("applicationId") final Integer applicationId
    ) {
        final String userName = authenticatedUserData.getName();
        LOGGER.info("User {} reserve resource id {} request", userName, applicationId);
        resourceMonitor.reserve(userName, applicationId);
    }

    @DELETE
    @Path("/application/reserve/{applicationId}")
    public void free(@Auth final AuthenticatedUserData authenticatedUserData,
                     @PathParam("applicationId") final Integer applicationId
    ) {
        final String userName = authenticatedUserData.getName();
        LOGGER.info("User {} free resource id {} request", userName, applicationId);
        resourceMonitor.free(userName, applicationId);
    }

    @POST
    @Path("/server/subscribe/{serverId}")
    public void subscribe(@Auth final AuthenticatedUserData authenticatedUserData,
                          @PathParam("serverId") final Integer serverId
    ) {
        final String userName = authenticatedUserData.getName();
        LOGGER.info("User {} subscribe on server id {} request", userName, serverId);
        resourceMonitor.subscribe(userName, serverId);
    }

    @DELETE
    @Path("/server/subscribe/{serverId}")
    public void unsubscribe(@Auth final AuthenticatedUserData authenticatedUserData,
                            @PathParam("serverId") final Integer serverId) {
        final String userName = authenticatedUserData.getName();
        LOGGER.info("User {} unsubscribe from server id {} request", userName, serverId);
        resourceMonitor.unsubscribe(userName, serverId);
    }

    @POST
    @Path("/application/action/{applicationId}")
    public void newInfo(
            @Auth final AuthenticatedUserData authenticatedUserData,
            @PathParam("applicationId") final Integer applicationId,
            @HeaderParam("ActionDetails") final String details
    ) {
        final String userName = authenticatedUserData.getName();
        LOGGER.info("User {} new action on resource id {} ({}) request", userName, applicationId, details);
        resourceMonitor.newAction(userName, applicationId, details);
    }

    @GET
    public EnvironmentsInfo getServerInfo(@Auth final AuthenticatedUserData authenticatedUserData) {
        LOGGER.info("User {} status request", authenticatedUserData.getName());
        return resourceMonitor.status();
    }

    @GET
    @Path("/application/action/{applicationId}")
    public ActionsInfo getActionsInfo(
            @Auth final AuthenticatedUserData authenticatedUserData,
            @PathParam("applicationId") final Integer applicationId,
            @HeaderParam("ActionsFrom") final String encodedBase64From,
            @HeaderParam("ActionsTo") final String encodedBase64To
    ) {
        final Instant fromInstant = ZonedDateTime.parse(decodeBase64String(encodedBase64From)).toInstant();
        final Instant toInstant = ZonedDateTime.parse(decodeBase64String(encodedBase64To)).toInstant();

        LOGGER.info("User {} actions on resource {} from {} to {} request",
                authenticatedUserData.getName(),
                applicationId,
                fromInstant,
                toInstant
        );

        return resourceMonitor.actionsInfo(applicationId, Range.closed(fromInstant, toInstant));
    }

    private String decodeBase64String(final String encodedString) {
        return new String(
                Base64.getDecoder().decode(encodedString),
                Charset.forName("UTF-8"));
    }
}
