package com.home.teamnotifier.web.rest;

import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;
import com.home.teamnotifier.authentication.AuthenticationInfo;
import com.home.teamnotifier.authentication.BasicAuthenticated;
import com.home.teamnotifier.authentication.TokenAuthenticated;
import com.home.teamnotifier.authentication.TokenCreator;
import com.home.teamnotifier.core.responses.authentication.UserInfo;
import com.home.teamnotifier.gateways.UserGateway;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.basic.BasicCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
        userGateway.newUser(credentials.getUsername(), credentials.getPassword());
    }

    @GET
    @Path("/whoami")
    public UserInfo whoAmI(@Auth final TokenAuthenticated userPrincipal) {
        final String name = userPrincipal.getName();
        LOGGER.info("WhoAmI request from {}", name);
        return new UserInfo(name);
    }
}
