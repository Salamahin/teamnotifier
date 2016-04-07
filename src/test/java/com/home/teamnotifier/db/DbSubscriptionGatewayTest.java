package com.home.teamnotifier.db;

import com.home.teamnotifier.DbPreparer;
import com.home.teamnotifier.gateways.exceptions.*;
import org.junit.Before;
import org.junit.Test;

import static com.home.teamnotifier.DbPreparer.getRandomString;
import static org.assertj.core.api.Assertions.assertThat;

public class DbSubscriptionGatewayTest {
    private static final DbPreparer helper = new DbPreparer();

    private EnvironmentEntity environmentEntity;

    private UserEntity userEntity;

    private DbSubscriptionGateway subscripbtion;

    @Test
    public void testReserve() throws Exception {
        final Integer resourceId = helper.anyResourceId(environmentEntity);
        subscripbtion.reserve(userEntity.getName(), resourceId);
    }

    @Test(expected = NotSubscribed.class)
    public void testUnsubscribeFromNotSubscribed() {
        final Integer serverId = helper.anyServerId(environmentEntity);
        final String userName = userEntity.getName();

        subscripbtion.unsubscribe(userName, serverId);
    }

    @Test(expected = AlreadySubscribed.class)
    public void testDoubleSubscribeCausesException() {
        final Integer serverId = helper.anyServerId(environmentEntity);
        final String userName = userEntity.getName();

        subscripbtion.subscribe(userName, serverId);
        subscripbtion.subscribe(userName, serverId);
    }

    @Test(expected = AlreadyReserved.class)
    public void testDoubleReserveCausesException()
            throws Exception {
        final String userName = userEntity.getName();
        final Integer resourceId = helper.anyResourceId(environmentEntity);

        subscripbtion.reserve(userName, resourceId);
        subscripbtion.reserve(userName, resourceId);
    }

    @Test
    public void testFree()
            throws Exception {
        final String userName = userEntity.getName();
        final Integer resourceId = helper.anyResourceId(environmentEntity);

        subscripbtion.reserve(userName, resourceId);
        subscripbtion.free(userName, resourceId);
    }

    @Test(expected = NotReserved.class)
    public void testFreeNotReservedResourceCausesException()
            throws Exception {
        final String userName = userEntity.getName();
        final Integer resourceId = helper.anyResourceId(environmentEntity);

        subscripbtion.free(userName, resourceId);
    }

    @Test
    public void testReturnsSubscribersNamesAfterSubscribe() throws Exception {
        final String userName1 = getPersistedUserName();
        final String userName2 = getPersistedUserName();

        final Integer serverId = helper.anyServerId(environmentEntity);

        assertThat(subscripbtion.subscribe(userName1, serverId).getSubscribers())
                .doesNotContain(userName1);
        assertThat(subscripbtion.subscribe(userName2, serverId).getSubscribers())
                .doesNotContain(userName2).contains(userName1);
    }

    private String getPersistedUserName() {
        return helper.createPersistedUser(getRandomString(), getRandomString()).getName();
    }

    @Test
    public void testReturnsSubscribersNamesAfterUnsubscribe()
            throws Exception {
        final String userName1 = getPersistedUserName();
        final String userName2 = getPersistedUserName();

        final Integer serverId = helper.anyServerId(environmentEntity);

        subscripbtion.subscribe(userName1, serverId);
        subscripbtion.subscribe(userName2, serverId);

        assertThat(subscripbtion.unsubscribe(userName1, serverId).getSubscribers())
                .doesNotContain(userName1).contains(userName2);
        assertThat(subscripbtion.unsubscribe(userName2, serverId).getSubscribers())
                .doesNotContain(userName2).doesNotContain(userName1);
    }

    @Test
    public void testReturnsSubscribersNamesAfterReserve()
            throws Exception {
        final String subscriber = getPersistedUserName();
        final Integer serverId = helper.anyServerId(environmentEntity);
        final Integer resourceId = helper.anyResourceId(environmentEntity, serverId);

        subscripbtion.subscribe(subscriber, serverId);

        assertThat(subscripbtion.reserve(userEntity.getName(), resourceId).getSubscribers())
                .contains(subscriber);
    }

    @Test
    public void testReturnsSubscribersNamesAfterFree()
            throws Exception {
        final String subscriber = getPersistedUserName();
        final Integer serverId = helper.anyServerId(environmentEntity);
        final Integer resourceId = helper.anyResourceId(environmentEntity);

        subscripbtion.subscribe(subscriber, serverId);
        subscripbtion.reserve(userEntity.getName(), resourceId);

        assertThat(subscripbtion.free(userEntity.getName(), resourceId).getSubscribers())
                .contains(subscriber);
    }

    @Test(expected = NoSuchServer.class)
    public void testExceptionWhenSubscribeToNotPresentServer() {
        final Integer serverId = -1;
        final String subscriber = getPersistedUserName();
        subscripbtion.subscribe(subscriber, serverId);
    }


    @Test(expected = NoSuchUser.class)
    public void testExceptionWhenSubscribeWithNotPresentUser() {
        final Integer serverId = helper.anyServerId(environmentEntity);
        final String subscriber = getRandomString();
        subscripbtion.subscribe(subscriber, serverId);
    }

    @Test(expected = NoSuchUser.class)
    public void testNoSuchUserWhenUnsubscribeWithNotPresentUser() {
        final Integer serverId = helper.anyServerId(environmentEntity);
        final String subscriber = getRandomString();
        subscripbtion.unsubscribe(subscriber, serverId);
    }

    @Test(expected = NoSuchServer.class)
    public void testNoSuchServerWhenUnsubscribeFromNotPresentServer() {
        final Integer serverId = -1;
        final String subscriber = getPersistedUserName();
        subscripbtion.unsubscribe(subscriber, serverId);
    }

    @Test(expected = NoSuchServer.class)
    public void testNoSuchServerWhenSubscribeToNotPresentServer() {
        final Integer serverId = -1;
        final String subscriber = getPersistedUserName();
        subscripbtion.subscribe(subscriber, serverId);
    }

    @Test(expected = NoSuchUser.class)
    public void testNoSuchUserWhenReserveWithNotPresentUser() {
        final Integer resourceId = helper.anyResourceId(environmentEntity);
        subscripbtion.reserve(getRandomString(), resourceId);
    }

    @Test(expected = NoSuchUser.class)
    public void testNoSuchUserWhenFreeWithNotPresentUser() {
        final Integer resourceId = helper.anyResourceId(environmentEntity);
        subscripbtion.free(getRandomString(), resourceId);
    }

    @Test(expected = NoSuchResource.class)
    public void testNoResourceWhenReserveWithNotPresentResource() {
        final String userName = getPersistedUserName();
        subscripbtion.reserve(userName, -1);
    }

    @Test(expected = NoSuchResource.class)
    public void testNoSuchResourceWhenFreeWithNotPresentResource() {
        final String userName = getPersistedUserName();
        subscripbtion.free(userName, -1);
    }

    @Test(expected = ReservedByDifferentUser.class)
    public void testFreeWithDifferentUserCausesReservedByOtherUser() {
        final String userName1 = getPersistedUserName();
        final String userName2 = getPersistedUserName();

        final Integer resourceId = helper.anyResourceId(environmentEntity);

        subscripbtion.reserve(userName1, resourceId);
        subscripbtion.free(userName2, resourceId);
    }


    @Before
    public void setUp()
            throws Exception {
        userEntity = helper.createPersistedUser(getRandomString(), getRandomString());
        environmentEntity = helper.createPersistedEnvironmentWithOneServerAndOneResource(
                getRandomString(),
                getRandomString(),
                getRandomString()
        );
        subscripbtion = new DbSubscriptionGateway(helper.TRANSACTION_HELPER);
    }
}