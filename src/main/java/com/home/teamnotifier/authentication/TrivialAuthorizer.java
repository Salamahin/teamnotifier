package com.home.teamnotifier.authentication;

import io.dropwizard.auth.Authorizer;

public class TrivialAuthorizer implements Authorizer<User> {

  @Override
  public boolean authorize(User user, String role) {
    return role.equals(TeamNotifierRoles.USER);
  }
}