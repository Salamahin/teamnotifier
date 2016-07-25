package com.home.teamnotifier.authentication.application;

import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.validator.ExpiryValidator;
import com.google.common.base.Optional;
import com.google.inject.Provider;
import com.home.teamnotifier.db.UserEntity;
import com.home.teamnotifier.gateways.UserGateway;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

public class AppTokenAuthenticator implements Authenticator<JsonWebToken, AppTokenPrincipal> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppTokenAuthenticator.class);

    private final ExpiryValidator expiryValidator;
    private final UserGateway userGateway;
    private final Provider<HttpServletRequest> requestProvider;

    public AppTokenAuthenticator(
            final Provider<HttpServletRequest> requestProvider,
            final UserGateway userGateway
    ) {
        this.userGateway = userGateway;
        this.requestProvider = requestProvider;
        expiryValidator = new ExpiryValidator();
    }

    private UserEntity extractUser(final JsonWebToken jsonWebToken) {
        return userGateway.get(Integer.valueOf(jsonWebToken.claim().subject()));
    }

    private String extractUserEndpoint(final JsonWebToken jsonWebToken) {
        return (String) (jsonWebToken.claim().getParameter(AppTokenCreator.ENDPOINT));
    }


    @Override
    public Optional<AppTokenPrincipal> authenticate(final JsonWebToken jsonWebToken) throws AuthenticationException {
        expiryValidator.validate(jsonWebToken);

        try {
            final UserEntity user = extractUser(jsonWebToken);
            final String grantedEndpoint = extractUserEndpoint(jsonWebToken);
            final String userEndpoint = requestProvider.get().getRemoteAddr();

            if (!Objects.equals(grantedEndpoint, userEndpoint))
                throw new IllegalRequestEndpoint(String.format(
                        "Token was created for user with %s endpoint, but request was made from %s",
                        grantedEndpoint,
                        userEndpoint
                ));

            return Optional.of(new AppTokenPrincipal(user.getName(), user.getId()));
        } catch (Exception exc) {
            LOGGER.error("Invalid token", exc);
            return Optional.absent();
        }
    }
}
