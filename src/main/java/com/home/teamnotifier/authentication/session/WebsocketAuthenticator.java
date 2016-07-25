package com.home.teamnotifier.authentication.session;

import com.google.common.base.Optional;
import com.home.teamnotifier.authentication.session.SessionTokenPrincipal;
import io.dropwizard.auth.AuthenticationException;

public interface WebsocketAuthenticator {
    Optional<SessionTokenPrincipal> authenticate(final String credentials) throws AuthenticationException;
}
