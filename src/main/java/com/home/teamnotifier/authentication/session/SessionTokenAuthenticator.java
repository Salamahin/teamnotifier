package com.home.teamnotifier.authentication.session;

import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenParser;
import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenVerifier;
import com.github.toastshaman.dropwizard.auth.jwt.exceptions.InvalidSignatureException;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.validator.ExpiryValidator;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.home.teamnotifier.db.UserEntity;
import com.home.teamnotifier.gateways.UserGateway;
import com.home.teamnotifier.gateways.exceptions.NoSuchUser;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;


public class SessionTokenAuthenticator implements Authenticator<JsonWebToken, SessionTokenPrincipal>, WebsocketAuthenticator {

    private final ExpiryValidator expiryValidator;
    private final UserGateway userGateway;
    private final JsonWebTokenVerifier verifier;
    private final JsonWebTokenParser jsonWebTokenParser;

    @Inject
    public SessionTokenAuthenticator(
            final UserGateway userGateway,
            final JsonWebTokenVerifier verifier,
            final JsonWebTokenParser jsonWebTokenParser
    ) {
        this.userGateway = userGateway;
        this.verifier = verifier;
        this.jsonWebTokenParser = jsonWebTokenParser;
        expiryValidator = new ExpiryValidator();
    }

    @Override
    public Optional<SessionTokenPrincipal> authenticate(final String jwtToken) throws AuthenticationException {
        final JsonWebToken token = jsonWebTokenParser.parse(jwtToken);

        try {
            verifier.verifySignature(token);
        } catch (InvalidSignatureException exc) {
            throw new AuthenticationException(exc);
        }

        return authenticate(token);
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
