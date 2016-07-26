package com.home.teamnotifier;

import com.github.toastshaman.dropwizard.auth.jwt.JWTAuthFilter;
import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenParser;
import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenVerifier;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.home.teamnotifier.authentication.AnyPrincipal;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.PermitAllAuthorizer;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.auth.basic.BasicCredentials;

final class AuthFiltersFactory {
    private AuthFiltersFactory() {
        throw new AssertionError();
    }

    static <T extends AnyPrincipal> JWTAuthFilter<T> newJwtAuthFilter(
            final JsonWebTokenParser webTokenParser,
            final JsonWebTokenVerifier verifier,
            final Authenticator<JsonWebToken, T> authenticator
    ) {
        return new JWTAuthFilter.Builder<T>()
                .setTokenParser(webTokenParser)
                .setTokenVerifier(verifier)
                .setPrefix("Bearer")
                .setAuthenticator(authenticator)
                .setAuthorizer(new PermitAllAuthorizer<>())
                .buildAuthFilter();
    }

    static <T extends AnyPrincipal> BasicCredentialAuthFilter<T> newBasicAuthFilter(
            final Authenticator<BasicCredentials, T> authenticator
    ) {
        return new BasicCredentialAuthFilter.Builder<T>()
                .setAuthenticator(authenticator)
                .setPrefix("x-Basic")
                .setAuthorizer(new PermitAllAuthorizer<>())
                .buildAuthFilter();
    }
}
