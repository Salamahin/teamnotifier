package com.home.teamnotifier.gateways.exceptions;

public class AlreadyReserved extends RuntimeException {
    public AlreadyReserved(String message) {
        super(message);
    }
}
