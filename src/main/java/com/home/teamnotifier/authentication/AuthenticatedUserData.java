package com.home.teamnotifier.authentication;

import java.security.Principal;

public final class AuthenticatedUserData implements Principal {
    private final String name;

    public AuthenticatedUserData(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
