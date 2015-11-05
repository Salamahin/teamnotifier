package com.home.teamnotifier.db;

public class TransactionError extends RuntimeException {
  public TransactionError(final Exception exc) {
    super(exc);
  }
}
