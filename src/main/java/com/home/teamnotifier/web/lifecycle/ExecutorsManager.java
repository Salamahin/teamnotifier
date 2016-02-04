package com.home.teamnotifier.web.lifecycle;

import com.google.inject.Inject;
import io.dropwizard.lifecycle.Managed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class ExecutorsManager implements Managed {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorsManager.class);

    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutor;

    @Inject
    public ExecutorsManager(final ExecutorService executorService, final ScheduledExecutorService scheduledExecutor) {
        this.executorService = executorService;
        this.scheduledExecutor = scheduledExecutor;
    }

    @Override
    public void start() throws Exception {
        //nop
    }

    @Override
    public void stop() throws Exception {
        executorService.shutdown();
        scheduledExecutor.shutdown();
        LOGGER.info("Executors stopped");
    }
}
