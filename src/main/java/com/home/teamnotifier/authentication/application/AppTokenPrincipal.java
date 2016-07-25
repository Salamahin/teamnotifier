package com.home.teamnotifier.authentication.application;

import com.home.teamnotifier.authentication.AnyPrincipal;

public class AppTokenPrincipal extends AnyPrincipal {

    AppTokenPrincipal(final String name, final int id) {
        super(name, id);
    }
}
