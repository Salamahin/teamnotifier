package com.home.teamnotifier.core;

import com.google.inject.Inject;
import com.home.teamnotifier.core.responses.notification.ServerState;
import com.home.teamnotifier.db.ServerEntity;
import com.home.teamnotifier.gateways.ServerGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import static com.google.common.collect.ImmutableMap.copyOf;
import static com.home.teamnotifier.core.responses.notification.ServerState.offline;
import static com.home.teamnotifier.core.responses.notification.ServerState.online;
import static com.home.teamnotifier.utils.FutureUtils.allAsList;
import static java.util.stream.Collectors.toList;

public class ServerAvailabilityCheckerImpl implements ServerAvailabilityChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerAvailabilityCheckerImpl.class);

    private final ScheduledExecutorService executor;
    private final ServerGateway gateway;
    private final NotificationManager notificationManager;

    private final Map<ServerEntity, Boolean> statuses;
    private ScheduledFuture<?> routine;

    @Inject
    ServerAvailabilityCheckerImpl(
            final ScheduledExecutorService executor,
            final ServerGateway gateway,
            final NotificationManager notificationManager
    ) {
        this.executor = executor;
        this.gateway = gateway;
        this.notificationManager = notificationManager;

        statuses = new ConcurrentHashMap<>();
    }

    boolean isOnline(final String url) {
        try {
            try (BufferedInputStream r = new BufferedInputStream(openUrl(url).openStream())) {
                return r.read() != -1;
            }
        } catch (Exception exc) {
            return false;
        }
    }

    private URL openUrl(String url) throws IOException {
        final URL checkUrl = new URL(url);
        URLConnection con = checkUrl.openConnection();
        con.setConnectTimeout(100);
        con.setReadTimeout(100);
        return checkUrl;
    }

    private void checkStatusAndNotifyAboutChange(final ServerEntity serverEntity) {
        final boolean newStatus = isOnline(serverEntity.getStatusURL());
        synchronized (statuses) {
            final Boolean oldStatus = statuses.get(serverEntity);

            if (oldStatus == null) {
                LOGGER.debug("Server initial [{} {}] status is {}",
                        serverEntity.getEnvironment().getName(),
                        serverEntity.getName(),
                        newStatus
                );
                statuses.put(serverEntity, newStatus);
                return;
            }

            if (oldStatus != newStatus)
                updateStatusAndNotifyAboutChange(serverEntity, newStatus);
        }
    }

    private void updateStatusAndNotifyAboutChange(final ServerEntity serverEntity, final boolean newStatus) {
        LOGGER.info("Server [{} {}] status change: is available [{}]",
                serverEntity.getEnvironment().getName(),
                serverEntity.getName(),
                newStatus
        );
        statuses.put(serverEntity, newStatus);
        notificationManager.pushToClients(
                serverEntity.getImmutableSetOfSubscribers(),
                buildMessage(newStatus, serverEntity)
        );
    }

    private ServerState buildMessage(final boolean isOnline, final ServerEntity server) {
        if (isOnline)
            return online(server);
        else
            return offline(server);
    }

    private Runnable routine() {
        return () -> {
            final Set<ServerEntity> observableServers = gateway.getImmutableSetOfObservableServers();

            final List<CompletableFuture<Void>> futures = observableServers.stream()
                    .map(s -> CompletableFuture.runAsync(() -> checkStatusAndNotifyAboutChange(s), executor))
                    .collect(toList());

            allAsList(futures, executor).join();

            LOGGER.debug("Current server states: " + statuses.entrySet().stream()
                    .map(e -> String.format("%s\t%s", e.getKey().getName(), e.getValue()))
                    .reduce((s1, s2) -> s1 + "\n" + s2)
                    .orElse("")
            );
        };
    }

    @Override
    public Map<ServerEntity, Boolean> getAvailability() {
        synchronized (statuses) {
            return copyOf(statuses);
        }
    }

    @Override
    public String report() {
        synchronized (statuses) {
            return statuses.entrySet().stream()
                    .map(e -> String.format("[%s]: %s", e.getKey(), e.getValue()))
                    .reduce((s1, s2) -> s1 + "; " + s2)
                    .orElse("");
        }
    }

    @Override
    public void start() {
        final int initialDelaySec = 0;
        final int delayBetweenSeriesSec = 30;

        if (routine != null) return;

        routine = executor.scheduleWithFixedDelay(
                routine(),
                initialDelaySec,
                delayBetweenSeriesSec,
                TimeUnit.SECONDS
        );
    }

    @Override
    public void stop() {
        if (routine == null)
            return;

        routine.cancel(true);
        routine = null;
    }
}
