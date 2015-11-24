package com.home.teamnotifier.gateways;

public class NotReserved extends RuntimeException {
    public NotReserved(String message) {
        super(message);
    }
}
