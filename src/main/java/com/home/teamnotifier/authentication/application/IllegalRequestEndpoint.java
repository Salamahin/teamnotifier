package com.home.teamnotifier.authentication.application;

class IllegalRequestEndpoint extends RuntimeException {
    IllegalRequestEndpoint(String message) {
        super(message);
    }
}
