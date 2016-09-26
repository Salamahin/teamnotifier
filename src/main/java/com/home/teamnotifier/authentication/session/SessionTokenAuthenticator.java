package com.home.teamnotifier.authentication.session;

import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.validator.ExpiryValidator;
import com.google.common.base.Optional;
import com.home.teamnotifier.db.UserEntity;
import com.home.teamnotifier.gateways.UserGateway;
import com.home.teamnotifier.gateways.exceptions.NoSuchUser;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;



public class SessionTokenAuthenticator implements Authenticator<JsonWebToken, SessionTokenPrincipal> {

    private final ExpiryValidator expiryValidator;
    private final UserGateway userGateway;

    public SessionTokenAuthenticator(final UserGateway userGateway) {
        this.userGateway = userGateway;
        expiryValidator = new ExpiryValidator();
    }

    @Override
    public Optional<SessionTokenPrincipal> authenticate(final JsonWebToken credentials) throws AuthenticationException {
        expiryValidator.validate(credentials);

        final int userId = Integer.valueOf(credentials.claim().subject());
        final UserEntity user;

        try {
            user = userGateway.get(userId);
        } catch (NoSuchUser e) {
            return Optional.absent();
        }

        return Optional.of(new SessionTokenPrincipal(user.getName(), userId));
    }
}
