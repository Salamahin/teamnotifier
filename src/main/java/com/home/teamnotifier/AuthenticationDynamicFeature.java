package com.home.teamnotifier;

import com.github.toastshaman.dropwizard.auth.jwt.JWTAuthFilter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.servlet.RequestScoped;
import com.home.teamnotifier.authentication.AnyPrincipal;
import com.home.teamnotifier.authentication.application.AppTokenPrincipal;
import com.home.teamnotifier.authentication.basic.BasicPrincipal;
import com.home.teamnotifier.authentication.session.SessionTokenPrincipal;
import com.home.teamnotifier.repo.PolymorphicAuthDynamicFeature;
import com.home.teamnotifier.repo.PolymorphicAuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

@Provider
class AuthenticationDynamicFeature extends PolymorphicAuthDynamicFeature<AnyPrincipal> {

    @Inject
    public AuthenticationDynamicFeature(
            final JWTAuthFilter<AnyPrincipal> jwtFilter,
            final BasicCredentialAuthFilter<BasicPrincipal> basicFilter,
            final Environment environment
    ) {
        super(ImmutableMap.of(
                SessionTokenPrincipal.class, jwtFilter,
                AppTokenPrincipal.class, jwtFilter,
                BasicPrincipal.class, basicFilter
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
