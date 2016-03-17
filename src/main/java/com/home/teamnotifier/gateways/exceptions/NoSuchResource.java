package com.home.teamnotifier.gateways.exceptions;

public class NoSuchResource extends RuntimeException {
    public NoSuchResource(String message) {
        super(message);
    }
}
