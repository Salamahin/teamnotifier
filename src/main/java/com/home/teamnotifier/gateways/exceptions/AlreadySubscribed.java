package com.home.teamnotifier.gateways.exceptions;

public class AlreadySubscribed extends RuntimeException {
    public AlreadySubscribed(String message, Throwable cause) {
        super(message, cause);
    }
}
