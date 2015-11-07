package com.home.teamnotifier.web.socket;

import com.google.common.collect.*;
import com.google.inject.Inject;
import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.*;
import java.io.IOException;
import java.util.Collection;

public class ClientManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(ClientManager.class);

  private final BiMap<Session, String> clients;

  public synchronized void addNewClient(final Session session, final String userName) {
    clients.put(session, userName);
  }

  public synchronized void removeClient(final Session session) {
    clients.remove(session);
  }

  @Inject
  public ClientManager() {
    clients = HashBiMap.create();
  }

  public synchronized void pushStringToAll(final String message) {
    clients.forEach((session, s) -> pushStringToClient(message, session));
  }

  public synchronized void pushToClients(final Collection<String> userNames, final String message) {
    final BiMap<String, Session> clientsByNames = clients.inverse();
    userNames.stream()
        .filter(clientsByNames::containsKey)
        .map(clientsByNames::get)
        .forEach(s -> pushStringToClient(message, s));
  }


  private void pushStringToClient(final String message, final Session session) {
    try {
      session.getRemote().sendString(message);
    } catch (IOException e) {
      LOGGER.error(String.format("Failed to send to %s", session.getRemote()), e);
    }
  }
}
