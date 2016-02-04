package com.home.teamnotifier.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.home.teamnotifier.core.responses.notification.EventType;
import com.home.teamnotifier.core.responses.notification.NotificationInfo;
import com.home.teamnotifier.db.AppServerEntity;
import com.home.teamnotifier.gateways.AppServerGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static com.home.teamnotifier.utils.FutureUtils.allAsList;
import static java.util.stream.Collectors.toList;

public class AppServerAvailabilityChecker {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppServerAvailabilityChecker.class);

    private final ScheduledExecutorService executor;
    private final AppServerGateway gateway;
    private final NotificationManager notificationManager;

    private final Map<Integer, Boolean> statuses;
    private ScheduledFuture<?> routine;

    @Inject
    public AppServerAvailabilityChecker(
            final ScheduledExecutorService executor,
            final AppServerGateway gateway,
            final NotificationManager notificationManager
    ) {
        this.executor = executor;
        this.gateway = gateway;
        this.notificationManager = notificationManager;

        statuses = new HashMap<>();
    }

    boolean isOnline(final String url) {
        try {
            final URLConnection connection = new URL(url).openConnection();
            connection.setConnectTimeout(100);
            connection.connect();
            return true;
        } catch (MalformedURLException me) {
            throw new IllegalArgumentException(me);
        } catch (Exception e) {
            return false;
        }
    }

    private void checkStatusAndNotifyAboutChange(final AppServerEntity serverEntity) {
        final boolean newStatus = isOnline(serverEntity.getStatusURL());
        synchronized (statuses) {
            final Boolean oldStatus = statuses.get(serverEntity.getId());

            if (oldStatus == null) {
                LOGGER.debug("Server initial [{} {}] status is {}",
                        serverEntity.getEnvironment().getName(),
                        serverEntity.getName(),
                        newStatus
                );
                statuses.put(serverEntity.getId(), newStatus);
                return;
            }

            if (oldStatus != newStatus)
                updateStatusAndNotifyAboutChange(serverEntity, newStatus);
        }
    }

    private void updateStatusAndNotifyAboutChange(final AppServerEntity serverEntity, final boolean newStatus) {
        LOGGER.info("Server [{} {}] status change: is available [{}]",
                serverEntity.getEnvironment().getName(),
                serverEntity.getName(),
                newStatus
        );
        statuses.put(serverEntity.getId(), newStatus);
        notificationManager.pushToClients(
                serverEntity.getImmutableListOfSubscribers(),
                buildMessage(newStatus, serverEntity.getId())
        );
    }

    private NotificationInfo buildMessage(final boolean isOnline, final int serverId) {
        return new NotificationInfo(
                null,
                Instant.now(),
                isOnline ? EventType.SERVER_ONLINE : EventType.SERVER_OFFLINE,
                serverId,
                ""
        );
    }

    private Runnable routine(final ImmutableList<AppServerEntity> servers) {
        return () -> {
            final List<CompletableFuture<Void>> futures = servers.stream()
                    .map(s -> CompletableFuture.runAsync(() -> checkStatusAndNotifyAboutChange(s), executor))
                    .collect(toList());

            allAsList(futures, executor).join();
        };
    }

    public ImmutableMap<Integer, Boolean> getAvailability() {
        synchronized (statuses) {
            return ImmutableMap.copyOf(statuses);
        }
    }

    public String report() {
        synchronized (statuses) {
            return statuses.entrySet().stream()
                    .map(e -> String.format("[%d]: %s", e.getKey(), e.getValue()))
                    .reduce((s1, s2) -> s1 + "; " + s2)
                    .orElse("");
        }
    }

    public void start() {
        if(routine != null)
            return;

        final ImmutableList<AppServerEntity> servers = gateway.getObservableServers();
        routine = executor.scheduleWithFixedDelay(routine(servers), 0, 5, TimeUnit.SECONDS);
    }

    public void stop() throws Exception {
        if(routine == null)
            return;

        routine.cancel(true);
        routine = null;
    }
}
