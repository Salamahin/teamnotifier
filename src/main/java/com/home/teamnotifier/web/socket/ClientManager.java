package com.home.teamnotifier.web.socket;

import com.google.common.collect.*;
import com.google.inject.Inject;
import com.home.teamnotifier.core.NotificationManager;
import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.*;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class ClientManager implements NotificationManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(ClientManager.class);

  private final Executor executor;

  private final AtomicReference<BiMap<Session, String>> clientSessionsByUsernames;

  @Inject
  public ClientManager(final Executor executor) {
    this.executor = executor;
    clientSessionsByUsernames = new AtomicReference<>(HashBiMap.create());
  }

  public synchronized void addNewClient(final Session session, final String userName) {
    clientSessionsByUsernames.get().put(session, userName);
  }

  public synchronized void removeClient(final Session session) {
    clientSessionsByUsernames.get().remove(session);
  }

  @Override
  public synchronized void pushToClients(final Collection<String> userNames, final String message) {
    final BiMap<String, Session> clientsByNames = clientSessionsByUsernames.get().inverse();
    userNames.stream()
        .filter(clientsByNames::containsKey)
        .map(clientsByNames::get)
        .forEach(s -> pushAsync(message, s));
  }

  private void pushAsync(final String message, final Session session) {
    CompletableFuture.runAsync(() -> pushSync(session, message), executor);
  }

  private void pushSync(final Session session, final String message) {
    try {
      session.getRemote().sendString(message);
    } catch (IOException e) {
      LOGGER.error(String.format("Failed to push to %s: ", clientSessionsByUsernames.get().get(session)),
          e);
    }
  }
}
