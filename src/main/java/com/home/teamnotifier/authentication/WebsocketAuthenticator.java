package com.home.teamnotifier.authentication;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;

public interface WebsocketAuthenticator {
    Optional<TokenAuthenticated> authenticate(final String credentials) throws AuthenticationException;
}
