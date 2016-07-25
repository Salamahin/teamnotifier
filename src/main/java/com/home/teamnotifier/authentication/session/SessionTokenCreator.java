package com.home.teamnotifier.authentication.session;

import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenSigner;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebTokenClaim;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebTokenHeader;
import com.google.inject.Inject;
import org.joda.time.DateTime;

public class SessionTokenCreator {

    private final JsonWebTokenSigner signer;
    private final JsonWebTokenHeader jsonWebTokenHeader;

    @Inject
    public SessionTokenCreator(final JsonWebTokenSigner signer, final JsonWebTokenHeader jsonWebTokenHeader) {
        this.signer = signer;
        this.jsonWebTokenHeader = jsonWebTokenHeader;
    }

    public String getTokenFor(final int userId) {
        final JsonWebToken token = JsonWebToken.builder()
                .header(jsonWebTokenHeader)
                .claim(JsonWebTokenClaim.builder()
                        .subject(String.valueOf(userId))
                        .issuedAt(DateTime.now())
                        .expiration(DateTime.now().plusYears(1))
                        .build())
                .build();

        return signer.sign(token);
    }
}
