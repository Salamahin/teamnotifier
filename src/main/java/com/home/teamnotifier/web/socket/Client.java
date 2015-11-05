package com.home.teamnotifier.web.socket;

import org.eclipse.jetty.websocket.api.Session;
import org.slf4j.*;
import java.io.IOException;

public class Client {
  private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);

  private final Session session;

  public Client(final Session session) {
    this.session = session;
  }

  public void pushString(String str) {
    try {
      session.getRemote().sendString(str);
    } catch (IOException e) {
      LOGGER.error("Error occurred", e);
    }
  }
}
