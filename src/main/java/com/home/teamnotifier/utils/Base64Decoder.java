package com.home.teamnotifier.utils;

import java.nio.charset.Charset;
import java.util.Base64;

public final class Base64Decoder {
    private Base64Decoder() {
        throw new AssertionError();
    }

    public static String decodeBase64(final String encoded) {
        return new String(Base64.getDecoder().decode(encoded), Charset.forName("UTF-8"));
    }
}
