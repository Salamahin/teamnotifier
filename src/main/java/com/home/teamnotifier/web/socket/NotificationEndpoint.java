package com.home.teamnotifier.web.socket;

import com.google.common.base.Optional;
import com.home.teamnotifier.authentication.AnyAuthenticated;
import com.home.teamnotifier.authentication.WebsocketAuthenticator;
import io.dropwizard.auth.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint("/")
public class NotificationEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationEndpoint.class);

    private final ClientManager clientManager;
    private final WebsocketAuthenticator authenticator;

    @Inject
    public NotificationEndpoint(final ClientManager clientManager, final WebsocketAuthenticator authenticator) {
        this.clientManager = clientManager;
        this.authenticator = authenticator;
    }

    private Optional<String> authenticate(final String token) {
        try {
            return authenticator.authenticate(token)
                    .transform(AnyAuthenticated::getName);

        } catch (AuthenticationException e) {
            LOGGER.error("Failed to extract websocket credentials", e);
            return Optional.absent();
        }
    }

    @SuppressWarnings("unused")
    @OnOpen
    public void open(@PathParam("token") final String token, final Session session) {
        final Optional<String> credentials = authenticate(token);
        if (credentials.isPresent()) {
            session.setMaxIdleTimeout(Long.MAX_VALUE);
            clientManager.addNewClient(session, credentials.get());
        } else {
            closeSessionWithAuthenticationError(session);
        }
    }

    private void closeSessionWithAuthenticationError(final Session session) {
        try {
            session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "authentication failed"));
        } catch (IOException e) {
            LOGGER.error("Failed to close websocket session", e);
        }
    }

    @SuppressWarnings("unused")
    @OnClose
    public void close(final Session session) {
        clientManager.removeClient(session);
    }

    @SuppressWarnings("unused")
    @OnError
    public void onError(final Throwable error) {
        LOGGER.error("Endpoint error", error);
    }
}
