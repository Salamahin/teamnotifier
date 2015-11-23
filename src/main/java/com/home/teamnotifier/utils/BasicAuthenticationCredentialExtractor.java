package com.home.teamnotifier.utils;

import com.google.common.base.Preconditions;
import io.dropwizard.auth.basic.BasicCredentials;

import static com.home.teamnotifier.utils.Base64Decoder.decodeBase64;

public final class BasicAuthenticationCredentialExtractor {
    private BasicAuthenticationCredentialExtractor() {
        throw new AssertionError();
    }

    public static BasicCredentials extract(final String encodedAuthorizationString) {
        Preconditions.checkNotNull(encodedAuthorizationString);
        Preconditions.checkArgument(encodedAuthorizationString.startsWith("Basic"));

        final String base64Credentials = encodedAuthorizationString.substring("Basic".length()).trim();
        final String credentials = decodeBase64(base64Credentials);

        final String[] values = credentials.split(":", 2);
        return new BasicCredentials(values[0], values[1]);
    }
}
