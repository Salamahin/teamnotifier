package com.home.teamnotifier.authentication;

import java.security.Principal;

public final class BasicPrincipal implements Principal {
    private final String name;
    private final int id;

    public BasicPrincipal(final String name, final int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
