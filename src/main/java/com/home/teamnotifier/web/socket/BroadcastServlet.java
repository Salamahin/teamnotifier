package com.home.teamnotifier.web.socket;

import com.google.common.base.Optional;
import com.home.teamnotifier.authentication.AnyAuthenticated;
import com.home.teamnotifier.authentication.TokenAuthenticated;
import com.home.teamnotifier.authentication.WebsocketAuthenticator;
import io.dropwizard.auth.AuthenticationException;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class BroadcastServlet extends WebSocketServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketHandler.class);

    private final ClientManager clientManager;
    private final WebsocketAuthenticator authenticator;

    public BroadcastServlet(
            final ClientManager clientManager,
            final WebsocketAuthenticator authenticator
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
            } catch (AuthenticationException exc) {
                LOGGER.warn("Failed to create websocket: authentication failed");
                sendUnauthorizedErrorResponse(resp, exc);
                return null;
            }
        });
    }

    private String tryGetAuthenticatedUserName(final ServletUpgradeRequest request) throws AuthenticationException {
        final String tokenStr = request.getParameterMap().get("token").get(0);
        final Optional<TokenAuthenticated> authenticatedUser = authenticator.authenticate(tokenStr);
        if (!authenticatedUser.isPresent()) {
            throw new AuthenticationException("Authentication failed");
        }
        return authenticatedUser.get().getName();
    }

    private void sendUnauthorizedErrorResponse(final ServletUpgradeResponse resp,
                                               final Exception exc) {
        try {
            resp.sendError(HttpStatus.UNAUTHORIZED_401, exc.getMessage());
        } catch (IOException e) {
            LOGGER.error("Failed to send error response", e);
        }
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
        }

        @Override
        public void onWebSocketClose(final int statusCode, final String reason) {
            manager.removeClient(session);
            LOGGER.info("[{}] Socket closed: [{}] {}", userName, statusCode, reason);
        }

        @Override
        public void onWebSocketConnect(final Session session) {
            this.session = session;
            session.setIdleTimeout(Long.MAX_VALUE);
            manager.addNewClient(session, userName);
            LOGGER.info("[{}] Socket connected", userName);
        }

        @Override
        public void onWebSocketError(final Throwable cause) {
            manager.removeClient(session);
            LOGGER.error("[{}] Websocket error ({})", userName, cause);
        }

        @Override
        public void onWebSocketText(final String message) {
        }
    }
}