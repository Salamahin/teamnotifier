package com.home.teamnotifier.web.rest;

import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;
import com.home.teamnotifier.authentication.AuthenticationInfo;
import com.home.teamnotifier.authentication.BasicAuthenticated;
import com.home.teamnotifier.authentication.TokenCreator;
import com.home.teamnotifier.gateways.UserGateway;
import com.home.teamnotifier.gateways.exceptions.InvalidCredentials;
import com.home.teamnotifier.gateways.exceptions.SuchUserAlreadyPresent;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.basic.BasicCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static com.home.teamnotifier.utils.BasicAuthenticationCredentialExtractor.extract;

@Path("1.0/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserRestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRestService.class);

    private final TokenCreator tokenCreator;
    private final UserGateway userGateway;

    @Inject
    public UserRestService(
            final TokenCreator tokenCreator,
            final UserGateway userGateway
    ) {
        this.tokenCreator = tokenCreator;
        this.userGateway = userGateway;
    }

    @GET
    @Path("/authenticate")
    public AuthenticationInfo authenticate(@Auth final BasicAuthenticated principal) {
        LOGGER.info("{} authenticated", principal.getName());
        return new AuthenticationInfo(tokenCreator.getTokenFor(principal.getId()));
    }

    @POST
    @Path("/register")
    public void newUser(@HeaderParam(HttpHeaders.AUTHORIZATION) final String encodedCredentials) {
        final BasicCredentials credentials = extract(encodedCredentials);
        LOGGER.info("New user {} register request", credentials.getUsername());
        try {
            userGateway.newUser(credentials.getUsername(), credentials.getPassword());
        } catch (RuntimeException exc) {
            LOGGER.error(String.format("Failed to create a new user %s", credentials.getUsername()), exc);
            handleUserCreationError(exc);
        }
    }

    private void handleUserCreationError(final RuntimeException exc) {
        try {
            throw exc;
        } catch (SuchUserAlreadyPresent e) {
            throw new WebApplicationException(Response.Status.CONFLICT);
        } catch (InvalidCredentials e) {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
    }
}
