package com.home.teamnotifier.core;

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
                LOGGER.debug("New server with id {} status {}", serverEntity.getId(), newStatus);
                statuses.put(serverEntity.getId(), newStatus);
                return;
            }

            if (oldStatus != newStatus)
                updateStatusAndNotifyAboutChange(serverEntity, newStatus);
        }
    }

    private void updateStatusAndNotifyAboutChange(final AppServerEntity serverEntity, final boolean newStatus) {
        LOGGER.info("Server id {} status change: is available [{}]", serverEntity.getId(), newStatus);
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

    private Runnable routine() {
        return () -> {
            final List<CompletableFuture<Void>> futures = gateway.getObservableServers().stream()
                    .map(s -> CompletableFuture.runAsync(() -> checkStatusAndNotifyAboutChange(s), executor))
                    .collect(toList());

            allAsList(futures, executor).join();
        };
    }

    public ImmutableMap<Integer, Boolean> getAvailability() {
        final HashMap<Integer, Boolean> transformedStatuses;
        synchronized (statuses) {
            transformedStatuses = statuses.entrySet().stream()
                    .collect(
                            HashMap::new,
                            (acc, e) -> acc.put(e.getKey(), e.getValue()),
                            Map::putAll
                    );
        }
        return ImmutableMap.copyOf(transformedStatuses);
    }

    public String report() {
        synchronized (statuses) {
            return statuses.entrySet().stream()
                    .map(e -> String.format("id %d: %s", e.getKey(), e.getValue()))
                    .reduce((s1, s2) -> s1 + "; " + s2)
                    .orElse("");
        }
    }

    public void start() {
        if(routine != null)
            return;

        routine = executor.scheduleWithFixedDelay(routine(), 0, 5, TimeUnit.SECONDS);
    }

    public void stop() throws Exception {
        if(routine == null)
            return;

        routine.cancel(true);
        routine = null;
    }
}
