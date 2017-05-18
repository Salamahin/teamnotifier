package com.home.teamnotifier.web.rest;

import com.google.common.net.HttpHeaders;
import com.home.teamnotifier.authentication.AuthenticationInfo;
import com.home.teamnotifier.authentication.basic.BasicPrincipal;
import com.home.teamnotifier.authentication.session.SessionTokenCreator;
import com.home.teamnotifier.gateways.UserGateway;
import com.home.teamnotifier.gateways.exceptions.InvalidCredentials;
import com.home.teamnotifier.gateways.exceptions.SuchUserAlreadyPresent;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.basic.BasicCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static com.home.teamnotifier.utils.BasicAuthenticationCredentialExtractor.extract;

@RestController
@RequestMapping("1.0/users")
public class UserRestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRestService.class);

    private final SessionTokenCreator sessionTokenCreator;
    private final UserGateway userGateway;

    @Autowired
    public UserRestService(
            final SessionTokenCreator sessionTokenCreator,
            final UserGateway userGateway
    ) {
        this.sessionTokenCreator = sessionTokenCreator;
        this.userGateway = userGateway;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/authenticate")
    public AuthenticationInfo authenticate(@AuthenticationPrincipal final BasicPrincipal principal) {
        LOGGER.info("{} authenticated", principal.getName());
        return new AuthenticationInfo(sessionTokenCreator.getTokenFor(principal.getId()));
    }

    @RequestMapping(method = RequestMethod.POST,path = "/register")
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
