package com.home.teamnotifier;

import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenParser;
import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenSigner;
import com.github.toastshaman.dropwizard.auth.jwt.JsonWebTokenVerifier;
import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA512Signer;
import com.github.toastshaman.dropwizard.auth.jwt.hmac.HmacSHA512Verifier;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebToken;
import com.github.toastshaman.dropwizard.auth.jwt.model.JsonWebTokenHeader;
import com.github.toastshaman.dropwizard.auth.jwt.parser.DefaultJsonWebTokenParser;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.home.teamnotifier.authentication.application.AppTokenAuthenticator;
import com.home.teamnotifier.authentication.application.AppTokenPrincipal;
import com.home.teamnotifier.authentication.basic.BasicAuthenticator;
import com.home.teamnotifier.authentication.basic.BasicPrincipal;
import com.home.teamnotifier.authentication.session.SessionTokenAuthenticator;
import com.home.teamnotifier.authentication.session.SessionTokenPrincipal;
import com.home.teamnotifier.authentication.session.WebSocketSessionAuthenticator;
import com.home.teamnotifier.core.NotificationManager;
import com.home.teamnotifier.core.ResourceMonitor;
import com.home.teamnotifier.core.ServerAvailabilityChecker;
import com.home.teamnotifier.core.ServerAvailabilityCheckerImpl;
import com.home.teamnotifier.db.*;
import com.home.teamnotifier.gateways.*;
import com.home.teamnotifier.web.socket.ClientManager;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

final class NotifierModule extends AbstractModule {

    @Override
    public void configure() {
        bind(UserGateway.class)
                .to(DbUserGateway.class)
                .in(Singleton.class);

        bind(EnvironmentGateway.class)
                .to(DbEnvironmentGateway.class)
                .in(Singleton.class);

        bind(ActionsGateway.class)
                .to(DbActionsGateway.class)
                .in(Singleton.class);

        bind(SubscriptionGateway.class)
                .to(DbSubscriptionGateway.class)
                .in(Singleton.class);

        bind(ServerGateway.class)
                .to(DbServerGateway.class)
                .in(Singleton.class);

        bind(NotificationManager.class)
                .to(ClientManager.class)
                .in(Singleton.class);

        bind(ServerAvailabilityChecker.class)
                .to(ServerAvailabilityCheckerImpl.class)
                .in(Singleton.class);

        bind(ClientManager.class)
                .in(Singleton.class);

        bind(ResourceMonitor.class)
                .in(Singleton.class);

        bind(TransactionHelper.class)
                .in(Singleton.class);

        bind(JsonWebTokenHeader.class)
                .toInstance(JsonWebTokenHeader.HS512());

        bind(JsonWebTokenParser.class)
                .to(DefaultJsonWebTokenParser.class);
    }

    @Provides
    @Singleton
    @SuppressWarnings("unused")
    public JsonWebTokenSigner newTokenSigner(final NotifierConfiguration configuration) {
        return new HmacSHA512Signer(configuration.getAuthenticationConfiguration().getJwtSecret().getBytes());
    }

    @Provides
    @Singleton
    @SuppressWarnings("unused")
    public JsonWebTokenVerifier newWebTokenVerifier(final NotifierConfiguration configuration) {
        return new HmacSHA512Verifier(configuration.getAuthenticationConfiguration().getJwtSecret().getBytes());
    }

    @Provides
    @Singleton
    @SuppressWarnings("unused")
    public ExecutorService newExecutor(final NotifierConfiguration configuration) {
        return Executors.newFixedThreadPool(
                configuration.getExecutorsConfiguration().getPoolSize(),
                new ThreadFactoryBuilder().setNameFormat("websocket-pool-%d").build()
        );
    }

    @Provides
    @Singleton
    @SuppressWarnings("unused")
    public ScheduledExecutorService newScheduledExecutor(final NotifierConfiguration configuration) {
        return Executors.newScheduledThreadPool(
                2,
                new ThreadFactoryBuilder().setNameFormat("url-checker-pool-%d").build()
        );
    }

    @Inject
    @Singleton
    @Provides
    @SuppressWarnings("unused")
    public Authenticator<JsonWebToken, AppTokenPrincipal> newAppAuthenticator(
            final Provider<HttpServletRequest> requestProvider,
            final UserGateway userGateway
    ) {
        return new AppTokenAuthenticator(requestProvider, userGateway);
    }

    @Inject
    @Singleton
    @Provides
    @SuppressWarnings("unused")
    public Authenticator<String, SessionTokenPrincipal> newWebSocketAuthenticator(
            final JsonWebTokenVerifier verifier,
            final JsonWebTokenParser parser,
            final Authenticator<JsonWebToken, SessionTokenPrincipal> authenticator
    ) {
        return new WebSocketSessionAuthenticator(verifier, parser, authenticator);
    }

    @Inject
    @Singleton
    @Provides
    @SuppressWarnings("unused")
    public Authenticator<JsonWebToken, SessionTokenPrincipal> newSessionAuthenticator(final UserGateway userGateway) {
        return new SessionTokenAuthenticator(userGateway);
    }

    @Provides
    @Singleton
    @Inject
    @SuppressWarnings("unused")
    public Authenticator<BasicCredentials, BasicPrincipal> newBasicAuthenticator(final UserGateway userGateway) {
        return new BasicAuthenticator(userGateway);
    }

}
