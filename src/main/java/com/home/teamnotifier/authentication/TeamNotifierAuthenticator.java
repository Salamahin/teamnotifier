package com.home.teamnotifier.authentication;

import com.google.common.base.*;
import com.google.common.hash.Hashing;
import com.google.inject.Inject;
import com.home.teamnotifier.gateways.*;
import com.home.teamnotifier.utils.PasswordHasher;
import io.dropwizard.auth.*;
import io.dropwizard.auth.basic.BasicCredentials;

public class TeamNotifierAuthenticator implements Authenticator<BasicCredentials, User> {
  private final UserGateway userGateway;

  @Inject
  public TeamNotifierAuthenticator(final UserGateway userGateway) {
    this.userGateway = userGateway;
  }

  @Override
  public Optional<User> authenticate(final BasicCredentials providedCredentials)
  throws AuthenticationException {
    final UserCredentials userCredentials = userGateway
        .userCredentials(providedCredentials.getUsername());
    if (userCredentials == null) {
      return Optional.absent();
    }

    final String providedHash = PasswordHasher.toMd5Hash(providedCredentials.getPassword());
    final String userHash = userCredentials.getPassHash();

    if (Objects.equal(userHash, providedHash)) {
      return Optional.of(new User(userCredentials.getUserName()));
    } else {
      return Optional.absent();
    }
  }
}