package com.home.teamnotifier.gateways;

public class AlreadyReserved extends RuntimeException {
    public AlreadyReserved(String message) {
        super(message);
    }
}
