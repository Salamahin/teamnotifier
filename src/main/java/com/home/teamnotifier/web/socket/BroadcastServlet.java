package com.home.teamnotifier.web.socket;

import com.google.common.base.Optional;
import com.home.teamnotifier.authentication.*;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.basic.BasicCredentials;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.servlet.*;
import org.slf4j.*;
import java.io.IOException;
import static com.home.teamnotifier.utils.BasicAuthenticationCredentialExtractor.extract;

public class BroadcastServlet extends WebSocketServlet {

  private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketHandler.class);

  private final ClientManager clientManager;

  private final TeamNotifierAuthenticator authenticator;

  public BroadcastServlet(
      final ClientManager clientManager,
      final TeamNotifierAuthenticator authenticator
  ) {
    this.clientManager = clientManager;
    this.authenticator = authenticator;
  }

  @Override
  public void configure(WebSocketServletFactory factory) {
    factory.register(WebSocketHandler.class);
    factory.setCreator((req, resp) -> {
      try {
        final String userName = tryGetAuthenticatedUserName(req);
        return new WebSocketHandler(clientManager, userName);
      } catch (Exception exc) {
        LOGGER.error("Websocket creation failed", exc);
        sendUnauthorizedErrorResponse(resp, exc);
        return null;
      }
    });
  }

  private void sendUnauthorizedErrorResponse(final ServletUpgradeResponse resp,
      final Exception exc) {
    try {
      resp.sendError(HttpStatus.UNAUTHORIZED_401, exc.getMessage());
    } catch (IOException e) {
      LOGGER.error("Failed to send error response", e);
    }
  }

  private String tryGetAuthenticatedUserName(final ServletUpgradeRequest request)
  throws AuthenticationException {
    //fixme dont know how to provide them different way; should be changed when ssl
    final String encodedCredentials = request.getParameterMap().get("credentials").get(0);
    final BasicCredentials credentials = extract(encodedCredentials);
    final Optional<User> authenticatedUser = authenticator.authenticate(credentials);
    if (!authenticatedUser.isPresent()) {
      throw new AuthenticationException("Authentication failed");
    }
    return authenticatedUser.get().getName();
  }

  private static class WebSocketHandler implements WebSocketListener {

    private final ClientManager manager;

    private final String userName;

    private Session session;

    public WebSocketHandler(final ClientManager manager, final String userName) {
      this.manager = manager;
      this.userName = userName;
    }

    @Override
    public void onWebSocketBinary(final byte[] payload, final int offset, final int len) {
         /* do nothing */
    }

    @Override
    public void onWebSocketClose(final int statusCode, final String reason) {
      manager.removeClient(session);
      LOGGER.info("Socket closed: [{}] {}", statusCode, reason);
    }

    @Override
    public void onWebSocketConnect(final Session session) {
      this.session = session;
      manager.addNewClient(session, userName);
      LOGGER.info("Socket connected: {}", Integer.toHexString(session.hashCode()));
    }

    @Override
    public void onWebSocketError(final Throwable cause) {
      manager.removeClient(session);
      LOGGER.error("Websocket error", cause);
    }

    @Override
    public void onWebSocketText(final String message) {
      LOGGER.info("Websocket text: {}", message);
    }
  }
}