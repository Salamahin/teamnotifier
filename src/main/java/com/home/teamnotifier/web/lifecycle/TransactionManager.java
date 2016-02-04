package com.home.teamnotifier.web.lifecycle;

import com.google.inject.Inject;
import com.home.teamnotifier.db.TransactionHelper;
import io.dropwizard.lifecycle.Managed;

public class TransactionManager implements Managed {
    private final TransactionHelper transactionHelper;

    @Inject
    public TransactionManager(final TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }

    @Override
    public void start() throws Exception {
        transactionHelper.start();
    }

    @Override
    public void stop() throws Exception {
        transactionHelper.stop();
    }
}
