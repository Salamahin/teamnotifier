package com.home.teamnotifier.web.socket;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.inject.Inject;
import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ClientManager
{
  private static final Logger LOGGER=LoggerFactory.getLogger(ClientManager.class);

  private final Executor executor;
  private final BiMap<Session, String> clientSessionsByUsernames;

  @Inject
  public ClientManager(Executor executor)
  {
    this.executor=executor;
    clientSessionsByUsernames=HashBiMap.create();
  }

  public synchronized void addNewClient(final Session session, final String userName)
  {
    clientSessionsByUsernames.put(session, userName);
  }

  public synchronized void removeClient(final Session session)
  {
    clientSessionsByUsernames.remove(session);
  }

  private void pushAsync(final String message, final Session session)
  {
    CompletableFuture.runAsync(() -> pushSync(session, message), executor);
  }

  public synchronized void pushToClients(final Collection<String> userNames, final String message)
  {
    final BiMap<String, Session> clientsByNames=clientSessionsByUsernames.inverse();
    userNames.stream()
        .filter(clientsByNames::containsKey)
        .map(clientsByNames::get)
        .forEach(s -> pushAsync(message, s));
  }

  private void pushSync(final Session session, final String message)
  {
    try
    {
      session.getRemote().sendString(message);
    }
    catch (IOException e)
    {
      LOGGER.error(String.format("Failed to push to %s: ", clientSessionsByUsernames.get(session)), e);
    }
  }
}
