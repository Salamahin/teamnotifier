package com.home.teamnotifier.core;

import com.google.common.collect.ImmutableSet;
import com.home.teamnotifier.core.responses.notification.NotificationInfo;
import com.home.teamnotifier.db.AppServerEntity;
import com.home.teamnotifier.gateways.AppServerGateway;
import org.junit.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AppServerAvailabilityCheckerTest {

    private static ServerSocket listeningSocket;
    private static AppServerAvailabilityChecker checker;
    private static ScheduledExecutorService executor;
    private Future<Socket> acceptFuture;

    @BeforeClass
    public static void setUp() throws Exception {
        final int HTTP_PORT = 8888;

        listeningSocket = new ServerSocket(HTTP_PORT);
        executor = Executors.newScheduledThreadPool(2);

        checker = new AppServerAvailabilityChecker(
                executor,
                new DummyServerGateway(),
                new DummyNotificationManager()
        );
    }

    @AfterClass
    public static void tearDown() throws Exception {
        listeningSocket.close();
    }

    @Before
    public void listen() throws IOException {
        acceptFuture = executor.submit(() -> listeningSocket.accept());
    }

    @After
    public void stopListening() throws Exception {
        acceptFuture.cancel(true);
    }

    @Test(timeout = 20000)
    public void testValidHostIsReachable() throws Exception {
        assertTrue(checker.isOnline("http://localhost:8888"));
    }

    @Test(timeout = 20000)
    public void testInvalidHostInUnreachable() throws Exception {
        assertFalse(checker.isOnline("http://localhost:8889"));
    }

    private static class DummyServerGateway implements AppServerGateway {
        @Override
        public Set<AppServerEntity> getImmutableSetOfObservableServers() {
            return ImmutableSet.of();
        }
    }

    private static class DummyNotificationManager implements NotificationManager {
        @Override
        public void pushToClients(Collection<String> userNames, NotificationInfo message) {
            //nop
        }
    }
}