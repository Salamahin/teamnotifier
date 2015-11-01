package com.home.teamnotifier.authentication;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

public class TeamNotifierAuthenticator implements Authenticator<BasicCredentials, User> {
  private final UserGateway userGateway;

  @Inject
  public TeamNotifierAuthenticator(final UserGateway userGateway) {
    this.userGateway = userGateway;
  }

  @Override
  public Optional<User> authenticate(BasicCredentials credentials)
  throws AuthenticationException {
    final java.util.Optional<User> user =
        userGateway.userByLoginPassword(credentials.getUsername(), credentials.getPassword());
    if (user.isPresent()) {
      return Optional.of(user.get());
    } else {
      return Optional.absent();
    }
  }
}