package com.home.teamnotifier.gateways;

public class AlreadySubscribed extends RuntimeException {
    public AlreadySubscribed(String message, Throwable cause) {
        super(message, cause);
    }
}
