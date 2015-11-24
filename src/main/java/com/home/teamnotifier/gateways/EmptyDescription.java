package com.home.teamnotifier.gateways;

import javax.validation.ConstraintViolationException;

public class EmptyDescription extends RuntimeException {
    public EmptyDescription(ConstraintViolationException exc) {
        super(exc);
    }
}
