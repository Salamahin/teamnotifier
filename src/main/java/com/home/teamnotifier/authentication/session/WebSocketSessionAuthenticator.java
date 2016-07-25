package com.home.teamnotifier.authentication.session;

import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenParser;
import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenVerifier;
import com.github.toastshaman.dropwizard.auth.jwt.exceptions.InvalidSignatureException;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

public class WebSocketSessionAuthenticator implements Authenticator<String, SessionTokenPrincipal> {
    private final JsonWebTokenVerifier verifier;
    private final JsonWebTokenParser jsonWebTokenParser;
    private final Authenticator<JsonWebToken, SessionTokenPrincipal> mainSessionAuthenticator;

    public WebSocketSessionAuthenticator(
            final JsonWebTokenVerifier verifier,
            final JsonWebTokenParser jsonWebTokenParser,
            final Authenticator<JsonWebToken, SessionTokenPrincipal> mainSessionAuthenticator
    ) {
        this.verifier = verifier;
        this.jsonWebTokenParser = jsonWebTokenParser;
        this.mainSessionAuthenticator = mainSessionAuthenticator;
    }

    @Override
    public Optional<SessionTokenPrincipal> authenticate(final String jwtToken) throws AuthenticationException {
        final JsonWebToken token = jsonWebTokenParser.parse(jwtToken);

        try {
            verifier.verifySignature(token);
        } catch (InvalidSignatureException exc) {
            throw new AuthenticationException(exc);
        }

        return mainSessionAuthenticator.authenticate(token);
    }

}
