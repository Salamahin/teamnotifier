package com.home.teamnotifier.authentication;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.hash.Hashing;
import com.google.inject.Inject;
import com.home.teamnotifier.gateways.UserCredentials;
import com.home.teamnotifier.gateways.UserGateway;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

public class TeamNotifierAuthenticator implements Authenticator<BasicCredentials, User>
{
  private final UserGateway userGateway;

  @Inject
  public TeamNotifierAuthenticator(final UserGateway userGateway)
  {
    this.userGateway=userGateway;
  }

  @Override
  public Optional<User> authenticate(BasicCredentials providedCredentials) throws AuthenticationException
  {
    final UserCredentials userCredentials=userGateway.userCredentials(providedCredentials.getUsername());
    if (userCredentials == null)
    {
      return Optional.absent();
    }

    final String providedHash=Hashing.md5().hashString(providedCredentials.getPassword(), Charsets.UTF_8).toString();
    final String userHash=userCredentials.getPassHash();

    if (Objects.equal(userHash, providedHash))
    {
      return Optional.of(new User(userCredentials.getUserName()));
    }
    else
    {
      return Optional.absent();
    }
  }
}