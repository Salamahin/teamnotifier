package com.home.teamnotifier.authentication;

import io.dropwizard.auth.Authorizer;

public class TrivialAuthorizer implements Authorizer<User> {

  @Override
  public boolean authorize(final User user, final String role) {
    return role.equals(TeamNotifierRoles.USER);
  }
}