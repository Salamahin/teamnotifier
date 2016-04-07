package com.home.teamnotifier.authentication;

import java.security.Principal;

public abstract class AnyAuthenticated implements Principal {
    private final String name;
    private final int id;

    public AnyAuthenticated(final String name, final int id) {
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }
}
