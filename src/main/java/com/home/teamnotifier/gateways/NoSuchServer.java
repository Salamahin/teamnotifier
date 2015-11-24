package com.home.teamnotifier.gateways;

public class NoSuchServer extends RuntimeException {
    public NoSuchServer(String message) {
        super(message);
    }
}
