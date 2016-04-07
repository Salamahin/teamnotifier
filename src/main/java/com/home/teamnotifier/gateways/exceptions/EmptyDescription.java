package com.home.teamnotifier.gateways.exceptions;

public class EmptyDescription extends RuntimeException {
    public EmptyDescription(Throwable exc) {
        super(exc);
    }
}
