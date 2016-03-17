package com.home.teamnotifier.gateways.exceptions;

public class NoSuchUser extends RuntimeException {
    public NoSuchUser(String message, Throwable cause) {
        super(message, cause);
    }
}
