package com.home.teamnotifier;

import com.github.toastshaman.dropwizard.auth.jwt.JWTAuthFilter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.home.teamnotifier.authentication.AnyAuthenticated;
import com.home.teamnotifier.authentication.BasicAuthenticated;
import com.home.teamnotifier.authentication.TokenAuthenticated;
import com.home.teamnotifier.repo.PolymorphicAuthDynamicFeature;
import com.home.teamnotifier.repo.PolymorphicAuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

import javax.inject.Inject;
import javax.ws.rs.ext.Provider;

@Provider
class AuthenticationDynamicFeature extends PolymorphicAuthDynamicFeature<AnyAuthenticated> {

    @Inject
    public AuthenticationDynamicFeature(
            final JWTAuthFilter<TokenAuthenticated> jwtTokenFilter,
            final BasicCredentialAuthFilter<BasicAuthenticated> basicFilter,
            final Environment environment
    ) {
        super(ImmutableMap.of(
                TokenAuthenticated.class, jwtTokenFilter,
                BasicAuthenticated.class, basicFilter
        ));

        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new PolymorphicAuthValueFactoryProvider.Binder<>(ImmutableSet.of(
                AnyAuthenticated.class,
                BasicAuthenticated.class,
                TokenAuthenticated.class
        )));
    }
}
