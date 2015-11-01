package com.home.teamnotifier.authentication;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;
import java.security.Principal;

@ToString
@EqualsAndHashCode
@Value
public class User implements Principal {
  String userName;

  String userSurname;

  @Override
  public String getName() {
    return userName;
  }
}
