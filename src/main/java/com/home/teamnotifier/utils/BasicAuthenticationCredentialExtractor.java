package com.home.teamnotifier.utils;

import com.google.common.base.Preconditions;
import io.dropwizard.auth.basic.BasicCredentials;
import java.nio.charset.Charset;
import java.util.Base64;

public final class BasicAuthenticationCredentialExtractor {
  private BasicAuthenticationCredentialExtractor() {
    throw new AssertionError();
  }

  public static BasicCredentials extract(final String encodedAuthorizationString) {
    Preconditions.checkNotNull(encodedAuthorizationString);
    Preconditions.checkArgument(encodedAuthorizationString.startsWith("Basic"));

    final String base64Credentials = encodedAuthorizationString.substring("Basic".length()).trim();
    final String credentials = new String(
        Base64.getDecoder().decode(base64Credentials),
        Charset.forName("UTF-8"));

    final String[] values = credentials.split(":", 2);
    return new BasicCredentials(values[0], values[1]);
  }
}
