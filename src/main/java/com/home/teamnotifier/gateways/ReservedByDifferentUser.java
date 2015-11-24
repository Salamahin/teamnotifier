package com.home.teamnotifier.gateways;

public class ReservedByDifferentUser extends RuntimeException {
    public ReservedByDifferentUser(String message) {
        super(message);
    }
}
