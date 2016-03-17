package com.home.teamnotifier.authentication;

import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenVerifier;
import com.github.toastshaman.dropwizard.auth.jwt.exceptions.InvalidSignatureException;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.parser.DefaultJsonWebTokenParser;
import com.github.toastshaman.dropwizard.auth.jwt.validator.ExpiryValidator;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.home.teamnotifier.db.UserEntity;
import com.home.teamnotifier.gateways.exceptions.NoSuchUser;
import com.home.teamnotifier.gateways.UserGateway;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

public class TokenAuthenticator implements Authenticator<JsonWebToken, TokenAuthenticated>, WebsocketAuthenticator {

    private final ExpiryValidator expiryValidator;
    private final UserGateway userGateway;
    private final JsonWebTokenVerifier verifier;

    @Inject
    public TokenAuthenticator(final UserGateway userGateway, final JsonWebTokenVerifier verifier) {
        this.userGateway = userGateway;
        this.verifier = verifier;
        expiryValidator = new ExpiryValidator();
    }

    @Override
    public Optional<TokenAuthenticated> authenticate(final String jwtToken) throws AuthenticationException {
        final JsonWebToken token = new DefaultJsonWebTokenParser().parse(jwtToken);

        try {
            verifier.verifySignature(token);
        } catch (InvalidSignatureException exc) {
            throw new AuthenticationException(exc);
        }

        return authenticate(token);
    }

    @Override
    public Optional<TokenAuthenticated> authenticate(final JsonWebToken credentials) throws AuthenticationException {
        expiryValidator.validate(credentials);

        final int userId = Integer.valueOf(credentials.claim().subject());
        final UserEntity user;

        try {
            user = userGateway.get(userId);
        } catch (NoSuchUser e) {
            return Optional.absent();
        }

        return Optional.of(new TokenAuthenticated(user.getName(), userId));
    }
}
