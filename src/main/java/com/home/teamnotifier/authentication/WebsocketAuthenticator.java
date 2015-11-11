package com.home.teamnotifier.authentication;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;

/**
 * Created by salamahin on 12.11.15.
 */
public interface WebsocketAuthenticator {
  Optional<User> authenticate(final String credentials) throws AuthenticationException;
}
