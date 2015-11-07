package com.home.teamnotifier.web.socket;

import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.servlet.*;
import org.slf4j.*;

public class BroadcastServlet extends WebSocketServlet {
  private final ClientManager clientManager;

  public BroadcastServlet(final ClientManager clientManager) {
    this.clientManager = clientManager;
  }

  @Override
  public void configure(WebSocketServletFactory factory) {
    factory.register(WebSocketHandler.class);
    factory.setCreator((req, resp) -> {
      System.out.println(req.getHeaders());
      return new WebSocketHandler(clientManager);});
  }

  private static class WebSocketHandler implements WebSocketListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketHandler.class);

    private final ClientManager manager;

    private Session session;

    public WebSocketHandler(final ClientManager manager) {
      this.manager = manager;
    }

    @Override
    public void onWebSocketBinary(final byte[] payload, final int offset, final int len) {
         /* do nothing */
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
      manager.removeClientBySession(session);
      LOGGER.info("Socket closed: [{}] {}", statusCode, reason);
    }

    @Override
    public void onWebSocketConnect(Session session) {
      this.session = session;
      manager.addNewClientBySession(session);
      LOGGER.info("Socket connected: {}", Integer.toHexString(session.hashCode()));
    }

    @Override
    public void onWebSocketError(Throwable cause) {
      manager.removeClientBySession(session);
      LOGGER.error("Websocket error", cause);
    }

    @Override
    public void onWebSocketText(final String message) {
      LOGGER.info("Websocket text: {}", message);
    }
  }
}