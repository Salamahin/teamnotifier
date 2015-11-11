package com.home.teamnotifier.authentication;

import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA512Signer;
import com.github.toastshaman.dropwizard.auth.jwt.model.*;
import com.google.inject.Inject;
import com.home.teamnotifier.NotifierConfiguration;
import org.joda.time.DateTime;

public class TokenCreator {

  private final HmacSHA512Signer signer;

  @Inject
  public TokenCreator(final NotifierConfiguration configuration) {
    signer = new HmacSHA512Signer(configuration.getJwtTokenSecret());
  }

  public String getTokenFor(final String userName) {
    final JsonWebToken token = JsonWebToken.builder()
        .header(JsonWebTokenHeader.HS512())
        .claim(JsonWebTokenClaim.builder()
            .subject(userName)
            .issuedAt(DateTime.now())
            .expiration(DateTime.now().plusHours(16))
            .build())
        .build();

    return signer.sign(token);
  }
}
