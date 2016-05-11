package com.home.teamnotifier.db;

import com.home.teamnotifier.gateways.UserGateway;
import com.home.teamnotifier.gateways.exceptions.*;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DbSubscriptionGatewayTest {
    private DbPreparer preparer;
    private UserGateway userGateway;
    private DbSubscriptionGateway gateway;

    @Before
    public void setUp() throws Exception {
        preparer = new DbPreparer();
        preparer.fillDb();

        gateway = new DbSubscriptionGateway(preparer.getTransactionHelper());
        userGateway = new DbUserGateway(preparer.getTransactionHelper());
    }

    @Test
    public void testReserve() throws Exception {
        final int resourceId = preparer.anyPersistedResourceId();
        gateway.reserve(preparer.persistedUserName(), resourceId);
    }

    @Test(expected = NotSubscribed.class)
    public void testUnsubscribeFromNotSubscribed() {
        final int serverId = preparer.anyPersistedServerId();
        final String userName = preparer.persistedUserName();

        gateway.unsubscribe(userName, serverId);
    }

    @Test(expected = AlreadySubscribed.class)
    public void testDoubleSubscribeCausesException() {
        final int serverId = preparer.anyPersistedServerId();
        final String userName = preparer.persistedUserName();

        gateway.subscribe(userName, serverId);
        gateway.subscribe(userName, serverId);
    }

    @Test(expected = AlreadyReserved.class)
    public void testDoubleReserveCausesException() throws Exception {
        final String userName = preparer.persistedUserName();
        final int resourceId = preparer.anyPersistedResourceId();

        gateway.reserve(userName, resourceId);
        gateway.reserve(userName, resourceId);
    }

    @Test
    public void testFree() throws Exception {
        final String userName = preparer.persistedUserName();
        final int resourceId = preparer.anyPersistedResourceId();

        gateway.reserve(userName, resourceId);
        gateway.free(userName, resourceId);
    }

    @Test(expected = NotReserved.class)
    public void testFreeNotReservedResourceCausesException()
            throws Exception {
        final String userName = preparer.persistedUserName();
        final int resourceId = preparer.anyPersistedResourceId();

        gateway.free(userName, resourceId);
    }

    @Test
    public void testReturnsSubscribersNamesAfterSubscribe() throws Exception {
        final String userName1 = persistNewUser();
        final String userName2 = persistNewUser();

        final int serverId = preparer.anyPersistedServerId();

        assertThat(gateway.subscribe(userName1, serverId).getSubscribers())
                .doesNotContain(userName1);
        assertThat(gateway.subscribe(userName2, serverId).getSubscribers())
                .doesNotContain(userName2).contains(userName1);
    }

    private String randomString () {
        return UUID.randomUUID().toString();
    }

    private String persistNewUser() {
        final String userName = randomString();
        userGateway.newUser(userName, randomString());
        return userName;
    }

    @Test
    public void testReturnsSubscribersNamesAfterUnsubscribe() throws Exception {
        final String userName1 = persistNewUser();
        final String userName2 = persistNewUser();

        final int serverId = preparer.anyPersistedServerId();

        gateway.subscribe(userName1, serverId);
        gateway.subscribe(userName2, serverId);

        assertThat(gateway.unsubscribe(userName1, serverId).getSubscribers())
                .doesNotContain(userName1).contains(userName2);
        assertThat(gateway.unsubscribe(userName2, serverId).getSubscribers())
                .doesNotContain(userName2).doesNotContain(userName1);
    }

    @Test
    public void testReturnsSubscribersNamesAfterReserve() throws Exception {
        final String subscriber = persistNewUser();
        final int serverId = preparer.anyPersistedServerId();
        final int resourceId = preparer.anyPersistedResourceId();

        gateway.subscribe(subscriber, serverId);

        assertThat(gateway.reserve(preparer.persistedUserName(), resourceId).getSubscribers())
                .contains(subscriber);
    }

    @Test
    public void testReturnsSubscribersNamesAfterFree() throws Exception {
        final String subscriber = persistNewUser();
        final int serverId = preparer.anyPersistedServerId();
        final int resourceId = preparer.anyPersistedResourceId();

        gateway.subscribe(subscriber, serverId);
        gateway.reserve(preparer.persistedUserName(), resourceId);

        assertThat(gateway.free(preparer.persistedUserName(), resourceId).getSubscribers())
                .contains(subscriber);
    }

    @Test(expected = NoSuchServer.class)
    public void testExceptionWhenSubscribeToNotPresentServer() {
        final int serverId = -1;
        final String subscriber = preparer.persistedUserName();
        gateway.subscribe(subscriber, serverId);
    }


    @Test(expected = NoSuchUser.class)
    public void testExceptionWhenSubscribeWithNotPresentUser() {
        final int serverId = preparer.anyPersistedServerId();
        gateway.subscribe("invalid_login", serverId);
    }

    @Test(expected = NoSuchUser.class)
    public void testNoSuchUserWhenUnsubscribeWithNotPresentUser() {
        final int serverId = preparer.anyPersistedServerId();
        gateway.unsubscribe("invalid_login", serverId);
    }

    @Test(expected = NoSuchServer.class)
    public void testNoSuchServerWhenUnsubscribeFromNotPresentServer() {
        final Integer serverId = -1;
        final String subscriber = preparer.persistedUserName();
        gateway.unsubscribe(subscriber, serverId);
    }

    @Test(expected = NoSuchServer.class)
    public void testNoSuchServerWhenSubscribeToNotPresentServer() {
        final Integer serverId = -1;
        final String subscriber = preparer.persistedUserName();
        gateway.subscribe(subscriber, serverId);
    }

    @Test(expected = NoSuchUser.class)
    public void testNoSuchUserWhenReserveWithNotPresentUser() {
        final Integer resourceId = preparer.anyPersistedResourceId();
        gateway.reserve("invalid_login", resourceId);
    }

    @Test(expected = NoSuchUser.class)
    public void testNoSuchUserWhenFreeWithNotPresentUser() {
        final Integer resourceId = preparer.anyPersistedResourceId();
        gateway.free("invalid_login", resourceId);
    }

    @Test(expected = NoSuchResource.class)
    public void testNoResourceWhenReserveWithNotPresentResource() {
        final String userName = preparer.persistedUserName();
        gateway.reserve(userName, -1);
    }

    @Test(expected = NoSuchResource.class)
    public void testNoSuchResourceWhenFreeWithNotPresentResource() {
        final String userName = preparer.persistedUserName();
        gateway.free(userName, -1);
    }

    @Test(expected = ReservedByDifferentUser.class)
    public void testFreeWithDifferentUserCausesReservedByOtherUser() {
        final String userName1 = persistNewUser();
        final String userName2 = persistNewUser();

        final Integer resourceId = preparer.anyPersistedResourceId();

        gateway.reserve(userName1, resourceId);
        gateway.free(userName2, resourceId);
    }
}