package com.home.teamnotifier.authentication;

import io.dropwizard.auth.Authorizer;

public class UserAuthorizer<T extends UserPrincipal> implements Authorizer<T> {

    @Override
    public boolean authorize(final T principal, final String role) {
        return true;
//        return principal.getOrigin().equalsIgnoreCase(role);
    }
}
