package com.home.teamnotifier;

import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenParser;
import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenVerifier;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.home.teamnotifier.authentication.AnyPrincipal;
import com.home.teamnotifier.authentication.application.AppTokenPrincipal;
import com.home.teamnotifier.authentication.basic.BasicPrincipal;
import com.home.teamnotifier.authentication.session.SessionTokenPrincipal;
import com.home.teamnotifier.repo.PolymorphicAuthDynamicFeature;
import com.home.teamnotifier.repo.PolymorphicAuthValueFactoryProvider;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

import static com.home.teamnotifier.AuthFiltersFactory.newBasicAuthFilter;
import static com.home.teamnotifier.AuthFiltersFactory.newJwtAuthFilter;

@Provider
class AuthenticationDynamicFeature extends PolymorphicAuthDynamicFeature<AnyPrincipal> {

    @Inject
    public AuthenticationDynamicFeature(
            final Environment environment,
            final JsonWebTokenParser parser,
            final JsonWebTokenVerifier verifier,
            final Authenticator<JsonWebToken, SessionTokenPrincipal> sessionTokenAuthenticator,
            final Authenticator<JsonWebToken, AppTokenPrincipal> appTokenAuthenticator,
            final Authenticator<BasicCredentials, BasicPrincipal> basicAuthenticator
    ) {
        super(ImmutableMap.of(
                SessionTokenPrincipal.class, newJwtAuthFilter(parser, verifier, sessionTokenAuthenticator),
                AppTokenPrincipal.class, newJwtAuthFilter(parser, verifier, appTokenAuthenticator),
                BasicPrincipal.class, newBasicAuthFilter(basicAuthenticator)
        ));

        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new PolymorphicAuthValueFactoryProvider.Binder<>(ImmutableSet.of(
                AnyPrincipal.class,
                BasicPrincipal.class,
                SessionTokenPrincipal.class,
                AppTokenPrincipal.class
        )));
    }
}
