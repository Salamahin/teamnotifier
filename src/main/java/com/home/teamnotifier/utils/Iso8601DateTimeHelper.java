package com.home.teamnotifier.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Iso8601DateTimeHelper {
    private Iso8601DateTimeHelper() {
        throw new AssertionError();
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static LocalDateTime parseTimestamp(final String timestamp) {
        return LocalDateTime.parse(timestamp, FORMATTER);
    }

    public static String toIso8601String(final LocalDateTime timestamp) {
        return timestamp.format(FORMATTER);
    }
}
