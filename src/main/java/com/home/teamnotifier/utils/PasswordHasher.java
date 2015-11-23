package com.home.teamnotifier.utils;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

public final class PasswordHasher {
    private PasswordHasher() {
        throw new IllegalStateException();
    }

    public static String toMd5Hash(final String password) {
        return Hashing.md5().hashString(password, Charsets.UTF_8).toString();
    }
}
