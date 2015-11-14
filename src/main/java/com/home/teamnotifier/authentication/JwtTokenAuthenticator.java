package com.home.teamnotifier.authentication;

import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.validator.ExpiryValidator;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.home.teamnotifier.gateways.*;
import io.dropwizard.auth.*;

public class JwtTokenAuthenticator
    implements Authenticator<JsonWebToken, AuthenticatedUserData>, WebsocketAuthenticator {

  private final ExpiryValidator expiryValidator;

  private final UserGateway userGateway;

  @Inject
  public JwtTokenAuthenticator(final UserGateway userGateway) {
    this.userGateway = userGateway;
    expiryValidator = new ExpiryValidator();
  }

  @Override
  public Optional<AuthenticatedUserData> authenticate(final String credentials)
  throws AuthenticationException {
    final String[] splitted = credentials.split(".");
    final JsonWebToken token = JsonWebToken.parser()
        .header(splitted[0])
        .claim(splitted[1])
        .signature(splitted[2].getBytes())
        .build();

    return authenticate(token);
  }

  @Override
  public Optional<AuthenticatedUserData> authenticate(final JsonWebToken credentials)
  throws AuthenticationException {
    expiryValidator.validate(credentials);

    final int userId = Integer.valueOf(credentials.claim().subject());
    final UserCredentials userCredentials = userGateway.userCredentials(userId);

    if (userCredentials == null) { return Optional.absent(); }

    return Optional.of(new AuthenticatedUserData(userCredentials.getUserName()));
  }
}
