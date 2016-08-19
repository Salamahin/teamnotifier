package com.home.teamnotifier.web.rest;

import com.google.inject.Inject;
import com.home.teamnotifier.authentication.AuthenticationInfo;
import com.home.teamnotifier.authentication.application.AppTokenCreator;
import com.home.teamnotifier.authentication.application.AppTokenPrincipal;
import com.home.teamnotifier.authentication.session.SessionTokenPrincipal;
import com.home.teamnotifier.core.ResourceMonitor;
import com.home.teamnotifier.gateways.ResourceDescription;
import io.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import static com.home.teamnotifier.utils.Base64Decoder.decodeBase64;

@Path("1.0/external")
@Produces(MediaType.APPLICATION_JSON)
public class ExternalRestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalRestService.class);

    private final ResourceMonitor resourceMonitor;
    private final AppTokenCreator creator;

    @Inject
    public ExternalRestService(final ResourceMonitor resourceMonitor, final AppTokenCreator creator) {
        this.resourceMonitor = resourceMonitor;
        this.creator = creator;
    }

    @POST
    @Path("/action")
    public void newResourceAction(
            @Auth final AppTokenPrincipal userPrincipal,
            @QueryParam("environment") final String environmentName,
            @QueryParam("server") final String serverName,
            @QueryParam("application") final String resourceName,
            @QueryParam("details") final String base64EncodedDetails
    ) {
        final String userName = userPrincipal.getName();
        final String decodedDetails = decodeBase64(base64EncodedDetails);

        LOGGER.info("User {} new action on resource {} {} ({}) request", userName, serverName, resourceName, decodedDetails);

        final ResourceDescription resourceDescription = ResourceDescription.newBuilder()
                .withResourceName(resourceName)
                .withServerName(serverName)
                .withEnvironmentName(environmentName)
                .build();

        resourceMonitor.newResourceAction(userName, resourceDescription, decodedDetails);
    }

    @GET
    @Path("/token")
    public AuthenticationInfo getApplicationToken(
            @Context final HttpServletRequest request,
            @Auth final SessionTokenPrincipal principal
    ) {
        LOGGER.info("{} requested new application token (endpoint %s)", principal.getName());
        return new AuthenticationInfo(creator.getTokenFor(principal.getId(), request.getRemoteAddr()));
    }
}
