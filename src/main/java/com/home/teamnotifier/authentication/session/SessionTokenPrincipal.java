package com.home.teamnotifier.authentication.session;

import com.home.teamnotifier.authentication.AnyPrincipal;

public class SessionTokenPrincipal extends AnyPrincipal {
    SessionTokenPrincipal(final String name, final int id) {
        super(name, id);
    }
}
