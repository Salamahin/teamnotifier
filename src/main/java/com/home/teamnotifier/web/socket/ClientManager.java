package com.home.teamnotifier.web.socket;

import com.google.inject.Inject;
import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.*;

public class ClientManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(ClientManager.class);

  private final Map<Session, Client> clients;

  private final DateTimeFormatter formatter = DateTimeFormatter
      .ofPattern("yyyy-MM-dd HH:mm:ss ")
      .withZone(ZoneId.systemDefault());

  @Inject
  public ClientManager(final ScheduledExecutorService scheduler) {
    clients = new ConcurrentHashMap<>();
    scheduler.scheduleAtFixedRate(this::broadcastTimeToAllClients, 0, 500,
        TimeUnit.MILLISECONDS);
  }

  private void broadcastTimeToAllClients() {
    if (clients.isEmpty()) {
      return;
    }
    try {
      LOGGER.trace("Broadcasting to {} clients", clients.size());
      String str = formatter.format(Instant.now());
      clients.forEach((s, c) -> c.pushString(str));
    } catch (Exception e) {
      LOGGER.error("Error occurred", e);
    }
  }

  public void addNewClientBySession(Session session) {
    clients.put(session, new Client(session));
  }

  public void removeClientBySession(Session session) {
    clients.remove(session);
  }
}
