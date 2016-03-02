package com.home.teamnotifier.authentication;

import io.dropwizard.auth.Authorizer;

public class UserAuthorizer implements Authorizer<UserPrincipal> {

    @Override
    public boolean authorize(final UserPrincipal principal, final String role) {
        return principal.getOrigin().equalsIgnoreCase(role);
    }
}
