package com.home.teamnotifier.gateways;

public class NotSubscribed extends RuntimeException {
    public NotSubscribed(String message) {
        super(message);
    }
}
