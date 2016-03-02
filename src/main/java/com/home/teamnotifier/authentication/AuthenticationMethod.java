package com.home.teamnotifier.authentication;

public final class AuthenticationMethod {
    private AuthenticationMethod() {
        throw new AssertionError();
    }

    public static final String BASIC_AUTHENTICATED = "";
    public static final String JWT_AUTHENTICATED = "";
}
