package com.home.teamnotifier.core;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.home.teamnotifier.core.responses.notification.ServerState;
import com.home.teamnotifier.db.ServerEntity;
import com.home.teamnotifier.gateways.ServerGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import static com.home.teamnotifier.utils.FutureUtils.allAsList;
import static java.util.stream.Collectors.toList;

public class ServerAvailabilityChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerAvailabilityChecker.class);

    private final ScheduledExecutorService executor;
    private final ServerGateway gateway;
    private final NotificationManager notificationManager;

    private final Map<ServerEntity, Boolean> statuses;
    private ScheduledFuture<?> routine;

    @Inject
    ServerAvailabilityChecker(
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
            final URL checkUrl = new URL(url);
            try (BufferedInputStream r = new BufferedInputStream(checkUrl.openStream())) {
                return r.read() != -1;
            }
        } catch (Exception exc) {
            return false;
        }
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
        if(isOnline)
            return ServerState.online(server);
        else
            return ServerState.offline(server);
    }

    private Runnable routine() {
        return () -> {
            final Set<ServerEntity> observableServers = gateway.getImmutableSetOfObservableServers();

            final List<CompletableFuture<Void>> futures = observableServers.stream()
                    .map(s -> CompletableFuture.runAsync(() -> checkStatusAndNotifyAboutChange(s), executor))
                    .collect(toList());

            allAsList(futures, executor).join();
        };
    }

    public ImmutableMap<ServerEntity, Boolean> getAvailability() {
        synchronized (statuses) {
            return ImmutableMap.copyOf(statuses);
        }
    }

    public String report() {
        synchronized (statuses) {
            return statuses.entrySet().stream()
                    .map(e -> String.format("[%s]: %s", e.getKey(), e.getValue()))
                    .reduce((s1, s2) -> s1 + "; " + s2)
                    .orElse("");
        }
    }

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

    public void stop() {
        if (routine == null)
            return;

        routine.cancel(true);
        routine = null;
    }
}
