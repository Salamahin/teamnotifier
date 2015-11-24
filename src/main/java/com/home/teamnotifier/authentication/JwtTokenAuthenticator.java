package com.home.teamnotifier.authentication;

import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenVerifier;
import com.github.toastshaman.dropwizard.auth.jwt.exceptions.InvalidSignatureException;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.parser.DefaultJsonWebTokenParser;
import com.github.toastshaman.dropwizard.auth.jwt.validator.ExpiryValidator;
import com.google.common.base.Optional;
import com.home.teamnotifier.gateways.UserCredentials;
import com.home.teamnotifier.gateways.UserGateway;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

public class JwtTokenAuthenticator
        implements Authenticator<JsonWebToken, AuthenticatedUserData>, WebsocketAuthenticator {

    private final ExpiryValidator expiryValidator;
    private final UserGateway userGateway;
    private final JsonWebTokenVerifier verifier;

    public JwtTokenAuthenticator(final UserGateway userGateway, final JsonWebTokenVerifier verifier) {
        this.userGateway = userGateway;
        this.verifier = verifier;
        expiryValidator = new ExpiryValidator();
    }

    @Override
    public Optional<AuthenticatedUserData> authenticate(final String jwtToken) throws AuthenticationException {
        final JsonWebToken token = new DefaultJsonWebTokenParser().parse(jwtToken);

        try {
            verifier.verifySignature(token);
        } catch (InvalidSignatureException exc) {
            throw new AuthenticationException(exc);
        }

        return authenticate(token);
    }

    @Override
    public Optional<AuthenticatedUserData> authenticate(final JsonWebToken credentials) throws AuthenticationException {
        expiryValidator.validate(credentials);

        final int userId = Integer.valueOf(credentials.claim().subject());
        final UserCredentials userCredentials = userGateway.userCredentials(userId);

        if (userCredentials == null) {
            return Optional.absent();
        }

        return Optional.of(new AuthenticatedUserData(userCredentials.getUserName()));
    }
}
