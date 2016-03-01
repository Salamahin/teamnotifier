package com.home.teamnotifier.authentication;

import java.security.Principal;

public final class OathPrincipal implements Principal {
    private final String name;

    public OathPrincipal(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
