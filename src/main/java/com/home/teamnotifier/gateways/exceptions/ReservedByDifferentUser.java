package com.home.teamnotifier.gateways.exceptions;

public class ReservedByDifferentUser extends RuntimeException {
    public ReservedByDifferentUser(String message) {
        super(message);
    }
}
