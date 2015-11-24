package com.home.teamnotifier.gateways;

public class NoSuchResource extends RuntimeException {
    public NoSuchResource(String message) {
        super(message);
    }
}
