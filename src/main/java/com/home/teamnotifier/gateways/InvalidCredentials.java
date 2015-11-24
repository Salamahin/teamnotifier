package com.home.teamnotifier.gateways;

public class InvalidCredentials extends RuntimeException {
    public InvalidCredentials(Throwable cause) {
        super(cause);
    }
}
