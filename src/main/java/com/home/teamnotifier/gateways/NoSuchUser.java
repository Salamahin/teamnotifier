package com.home.teamnotifier.gateways;

public class NoSuchUser extends RuntimeException {
    public NoSuchUser(String message, Throwable cause) {
        super(message, cause);
    }
}
