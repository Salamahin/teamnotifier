package com.home.teamnotifier.core;

import com.google.common.collect.ImmutableSet;
import com.home.teamnotifier.core.responses.notification.Notification;
import com.home.teamnotifier.core.responses.status.ServerInfo;
import com.home.teamnotifier.db.ServerEntity;
import com.home.teamnotifier.gateways.ServerGateway;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ServerAvailabilityCheckerTest {

    private static ServerAvailabilityChecker checker;
    private static Server server;

    private static class HelloHandler extends AbstractHandler {
        @Override
        public void handle(
                final String target,
                final Request baseRequest,
                final HttpServletRequest request,
                final HttpServletResponse response
        ) throws IOException, ServletException {
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);
            response.getWriter().println("<h1>Hello World</h1>");
        }
    }

    @BeforeClass
    public static void preinit() throws Exception {
        checker = new ServerAvailabilityChecker(
                Executors.newScheduledThreadPool(10),
                new DummyServerGateway(),
                new DummyNotificationManager()
        );
    }

    @Before
    public void setUp() throws Exception {
        server = new Server(8080);
        server.setHandler(new HelloHandler());

        server.start();
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    @Test(timeout = 20000)
    public void testValidHostIsReachable() throws Exception {
        assertTrue(checker.isOnline("http://localhost:8080"));
    }

    @Test
    public void testInvalidHostInUnreachable() throws Exception {
        assertFalse(checker.isOnline("http://localhost:8081"));
    }

    private static class DummyServerGateway implements ServerGateway {
        @Override
        public Set<ServerEntity> getImmutableSetOfObservableServers() {
            return ImmutableSet.of();
        }

        @Override
        public ServerInfo getInfoForServer(int id) {
            throw new AssertionError("not used for test case");
        }
    }

    private static class DummyNotificationManager implements NotificationManager {
        @Override
        public void pushToClients(Collection<String> userNames, Notification message) {
            //nop
        }
    }
}