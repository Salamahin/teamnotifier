package com.home.teamnotifier.gateways.exceptions;

public class NotSubscribed extends RuntimeException {
    public NotSubscribed(String message) {
        super(message);
    }
}
