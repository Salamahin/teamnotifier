package com.home.teamnotifier.authentication;

import java.security.Principal;

public final class UserPrincipal implements Principal {
    private final String name;
    private final int id;
    private final String origin;

    private UserPrincipal(final String name, final int id, final String origin) {
        this.name = name;
        this.id = id;
        this.origin = origin;
    }

    static UserPrincipal basic(final int id, final String name) {
        return new UserPrincipal(name, id, AuthenticationMethod.BASIC_AUTHENTICATED);
    }

    static UserPrincipal jwt(final int id, final String name) {
        return new UserPrincipal(name, id, AuthenticationMethod.JWT_AUTHENTICATED);
    }

    public int getId() {
        return id;
    }

    public String getOrigin() {
        return origin;
    }

    @Override
    public String getName() {
        return name;
    }
}
