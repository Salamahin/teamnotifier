package com.home.teamnotifier.web.rest;

import com.google.common.net.HttpHeaders;
import com.google.inject.Inject;
import com.home.teamnotifier.authentication.AuthenticatedUserData;
import com.home.teamnotifier.authentication.AuthenticationInfo;
import com.home.teamnotifier.authentication.TokenCreator;
import com.home.teamnotifier.core.responses.authentication.UserInfo;
import com.home.teamnotifier.gateways.UserCredentials;
import com.home.teamnotifier.gateways.UserGateway;
import com.home.teamnotifier.utils.PasswordHasher;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.basic.BasicCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Objects;

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
    public AuthenticationInfo authenticate(
            @HeaderParam(HttpHeaders.AUTHORIZATION) final String encodedCredentials
    ) {
        final BasicCredentials credentials = extract(encodedCredentials);
        final String username = credentials.getUsername();

        LOGGER.info("User {} authentication request", credentials.getUsername());

        final UserCredentials persistedCredentials = userGateway.userCredentials(username);

        if (compareCredentials(credentials, persistedCredentials)) {
            final String token = tokenCreator.getTokenFor(persistedCredentials.getId());
            LOGGER.info("User {} authentication success; token=[{}]",
                    credentials.getUsername(),
                    token
            );
            return new AuthenticationInfo(token);
        }
        LOGGER.error("User {} authentication failed", credentials.getUsername());


        return null;
    }

    private boolean compareCredentials(
            final BasicCredentials provided,
            final UserCredentials persisted
    ) {
        if (persisted == null) {
            return false;
        }

        final String providedPassHash = PasswordHasher.toMd5Hash(provided.getPassword());
        return Objects.equals(providedPassHash, persisted.getPassHash());
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
    public UserInfo whoAmI(@Auth final AuthenticatedUserData authenticatedUserData) {
        final String name = authenticatedUserData.getName();
        LOGGER.info("WhoAmI request from {}", name);
        return new UserInfo(name);
    }
}
