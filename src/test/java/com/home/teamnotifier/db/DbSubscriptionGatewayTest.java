package com.home.teamnotifier.db;

import com.google.common.collect.Lists;
import com.home.teamnotifier.core.BroadcastInformation;
import com.home.teamnotifier.core.SubscriptionActionResult;
import com.home.teamnotifier.core.responses.action.ServerSubscribersInfo;
import com.home.teamnotifier.core.responses.notification.Subscription;
import com.home.teamnotifier.gateways.UserGateway;
import com.home.teamnotifier.gateways.exceptions.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DbSubscriptionGatewayTest {
    private DbPreparer preparer;
    private UserGateway userGateway;
    private DbSubscriptionGateway gateway;

    @Before
    public void setUp() throws Exception {
        preparer = new DbPreparer();
        preparer.initDataBase();

        gateway = new DbSubscriptionGateway(preparer.getTransactionHelper());
        userGateway = new DbUserGateway(preparer.getTransactionHelper());
    }

    @Test
    public void testReserve() throws Exception {
        final int resourceId = preparer.persistedResourceId();
        gateway.reserve(preparer.persistedUserName(), resourceId);
    }

    @Test(expected = NotSubscribed.class)
    public void testUnsubscribeFromNotSubscribed() {
        final int serverId = preparer.persistedServerId();
        final String userName = preparer.persistedUserName();

        gateway.unsubscribe(userName, serverId);
    }

    @Test(expected = AlreadySubscribed.class)
    public void testDoubleSubscribeCausesException() {
        final int serverId = preparer.persistedServerId();
        final String userName = preparer.persistedUserName();

        gateway.subscribe(userName, serverId);
        gateway.subscribe(userName, serverId);
    }

    @Test(expected = AlreadyReserved.class)
    public void testDoubleReserveCausesException() throws Exception {
        final String userName = preparer.persistedUserName();
        final int resourceId = preparer.persistedResourceId();

        gateway.reserve(userName, resourceId);
        gateway.reserve(userName, resourceId);
    }

    @Test
    public void testFree() throws Exception {
        final String userName = preparer.persistedUserName();
        final int resourceId = preparer.persistedResourceId();

        gateway.reserve(userName, resourceId);
        gateway.free(userName, resourceId);
    }

    @Test(expected = NotReserved.class)
    public void testFreeNotReservedResourceCausesException()
            throws Exception {
        final String userName = preparer.persistedUserName();
        final int resourceId = preparer.persistedResourceId();

        gateway.free(userName, resourceId);
    }

    @Test
    public void testReturnsSubscribersNamesAfterSubscribe() throws Exception {
        final String userName1 = persistNewUser();
        final String userName2 = persistNewUser();

        final int serverId = preparer.persistedServerId();

        assertSubscribtionReturnContainsNecessarySubscribersNames(
                gateway.subscribe(userName1, serverId),
                Lists.newArrayList(userName1),
                Lists.newArrayList(userName1)
        );

        assertSubscribtionReturnContainsNecessarySubscribersNames(
                gateway.subscribe(userName2, serverId),
                Lists.newArrayList(userName1, userName2),
                Lists.newArrayList(userName2)
        );

    }

    private void assertSubscribtionReturnContainsNecessarySubscribersNames(
            final SubscriptionActionResult actionResult,
            final List<String> allExpectedSubscribers,
            final List<String> subscribersExcludedInBroadcastInfo
    ) {
        final ServerSubscribersInfo subscribersInfo = actionResult.getSubscribersInfo();
        assertThat(subscribersInfo.getSubscribers())
                .containsAll(allExpectedSubscribers);

        final List<String> expectedSubscribersInBroadcastInfo = Lists.newArrayList(allExpectedSubscribers);
        expectedSubscribersInBroadcastInfo.removeAll(subscribersExcludedInBroadcastInfo);

        final BroadcastInformation<Subscription> broadcastInformation = actionResult.getBroadcastInformation();
        assertThat(broadcastInformation.getSubscribers())
                .containsAll(expectedSubscribersInBroadcastInfo)
                .doesNotContainAnyElementsOf(subscribersExcludedInBroadcastInfo);
    }

    private String randomString() {
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

        final int serverId = preparer.persistedServerId();

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
        final int serverId = preparer.persistedServerId();
        final int resourceId = preparer.persistedResourceId();

        gateway.subscribe(subscriber, serverId);

        assertThat(gateway.reserve(preparer.persistedUserName(), resourceId).getSubscribers())
                .contains(subscriber);
    }

    @Test
    public void testReturnsSubscribersNamesAfterFree() throws Exception {
        final String subscriber = persistNewUser();
        final int serverId = preparer.persistedServerId();
        final int resourceId = preparer.persistedResourceId();

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
        final int serverId = preparer.persistedServerId();
        gateway.subscribe("invalid_login", serverId);
    }

    @Test(expected = NoSuchUser.class)
    public void testNoSuchUserWhenUnsubscribeWithNotPresentUser() {
        final int serverId = preparer.persistedServerId();
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
        final Integer resourceId = preparer.persistedResourceId();
        gateway.reserve("invalid_login", resourceId);
    }

    @Test(expected = NoSuchUser.class)
    public void testNoSuchUserWhenFreeWithNotPresentUser() {
        final Integer resourceId = preparer.persistedResourceId();
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

        final Integer resourceId = preparer.persistedResourceId();

        gateway.reserve(userName1, resourceId);
        gateway.free(userName2, resourceId);
    }
}