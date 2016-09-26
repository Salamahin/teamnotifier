package com.home.teamnotifier.web.lifecycle;

import com.google.inject.Inject;
import com.home.teamnotifier.db.TransactionHelper;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionManager implements Managed {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionManager.class);

    private final TransactionHelper transactionHelper;

    @Inject
    public TransactionManager(final TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }

    @Override
    public void start() throws Exception {
        //nop
    }

    @Override
    public void stop() throws Exception {
        transactionHelper.stop();
        LOGGER.info("Transactions interrupted");
    }
}
