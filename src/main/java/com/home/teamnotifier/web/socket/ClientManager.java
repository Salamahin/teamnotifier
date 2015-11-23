package com.home.teamnotifier.web.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.*;
import com.google.inject.Inject;
import com.home.teamnotifier.core.NotificationManager;
import com.home.teamnotifier.core.responses.action.ActionInfo;
import com.home.teamnotifier.core.responses.notification.NotificationInfo;
import io.dropwizard.jackson.Jackson;
import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.*;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class ClientManager implements NotificationManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(ClientManager.class);

  private final Executor executor;
  private final BiMap<Session, String> clientSessionsByUsernames;
  private final ObjectMapper mapper = Jackson.newObjectMapper();

  @Inject
  public ClientManager(final Executor executor) {
    this.executor = executor;
    clientSessionsByUsernames = HashBiMap.create();
  }

  public synchronized List<String> getClientNamesList() {
    return Lists.newArrayList(clientSessionsByUsernames.values());
  }

  public synchronized void addNewClient(final Session session, final String userName) {
    clientSessionsByUsernames.put(session, userName);
  }

  public synchronized void removeClient(final Session session) {
    clientSessionsByUsernames.remove(session);
  }

  @Override
  public synchronized void pushToClients(final Collection<String> userNames, final NotificationInfo message) {
    final BiMap<String, Session> clientsByNames = clientSessionsByUsernames.inverse();
    final String messageString = infoToString(message);
    userNames.stream()
        .filter(clientsByNames::containsKey)
        .map(clientsByNames::get)
        .forEach(s -> pushAsync(messageString, s));
  }

  private String infoToString(NotificationInfo message) {
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
    try {
      session.getRemote().sendString(message);
    } catch (IOException e) {
      LOGGER.error(String.format("Failed to push to %s: ", clientSessionsByUsernames.get(session)),
          e);
    }
  }
}
