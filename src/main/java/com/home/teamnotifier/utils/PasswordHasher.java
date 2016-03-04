package com.home.teamnotifier.utils;

import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

public final class PasswordHasher {
    private PasswordHasher() {
        throw new IllegalStateException();
    }

    public static String toHash(final String password, final String salt) {
        final String solted = salt + password;
        return Hashing.sha512().hashString(solted, Charset.forName("UTF-8")).toString();
    }
}
