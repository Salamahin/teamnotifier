package com.home.teamnotifier.web.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.home.teamnotifier.core.NotificationManager;
import com.home.teamnotifier.core.responses.notification.Notification;
import io.dropwizard.jackson.Jackson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static java.util.stream.Collectors.joining;

public class ClientManager implements NotificationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientManager.class);

    private final ExecutorService executor;
    private final BiMap<Session, String> clientSessionsByUsernames;
    private final ObjectMapper mapper = Jackson.newObjectMapper();

    @Inject
    public ClientManager(final ExecutorService executor) {
        this.executor = executor;
        clientSessionsByUsernames = HashBiMap.create();
    }

    public synchronized List<String> getClientNamesList() {
        return Lists.newArrayList(clientSessionsByUsernames.values());
    }

    synchronized void addNewClient(final Session session, final String userName) {
        clientSessionsByUsernames.forcePut(session, userName);
    }

    synchronized void removeClient(final Session session) {
        clientSessionsByUsernames.remove(session);
    }

    @Override
    public synchronized void pushToClients(final Collection<String> userNames, final Notification message) {
        final BiMap<String, Session> clientsByNames = clientSessionsByUsernames.inverse();
        final String messageString = infoToString(message);

        final String clientsList = clientsByNames.keySet().stream().collect(joining("\n"));
        LOGGER.debug("Message {} will be send to client list: \n{}", messageString, clientsList);

        userNames.stream()
                .filter(clientsByNames::containsKey)
                .map(clientsByNames::get)
                .forEach(s -> pushAsync(messageString, s));
    }

    private String infoToString(final Notification message) {
        try {
            return mapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed to map action info", e);
        }
        return "";
    }

    private void pushAsync(final String message, final Session session) {
        CompletableFuture.runAsync(() -> pushSync(session, message), executor);
    }

    private void pushSync(final Session session, final String message) {
        final String userName = clientSessionsByUsernames.get(session);
        try {
            LOGGER.debug("Pushing message {} to client {}", message, userName);
            session.getBasicRemote().sendText(message);
            LOGGER.debug("Message {} pushed to {}", message, userName);
        } catch (IOException e) {
            LOGGER.error(String.format("Failed to push message %s to %s: ", message, userName), e);
        }
    }
}
