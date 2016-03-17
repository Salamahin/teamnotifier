package com.home.teamnotifier.db;

import com.google.common.collect.Range;
import com.home.teamnotifier.DbPreparer;
import com.home.teamnotifier.core.BroadcastInformation;
import com.home.teamnotifier.core.responses.action.ActionsInfo;
import com.home.teamnotifier.gateways.ActionsGateway;
import com.home.teamnotifier.gateways.exceptions.EmptyDescription;
import com.home.teamnotifier.gateways.exceptions.NoSuchServer;
import com.home.teamnotifier.gateways.exceptions.NoSuchUser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;

import static com.home.teamnotifier.DbPreparer.getRandomString;

public class DbActionsOnServerGatewayTest {
    private static ActionsTester tester;
    private static DbPreparer helper;
    private static ActionsGateway gateway;
    private static EnvironmentEntity environment;

    @BeforeClass
    public static void setUp() throws Exception {
        helper = new DbPreparer();
        gateway = new DbActionsGateway(helper.TRANSACTION_HELPER);

        environment = helper.createPersistedEnvironmentWithOneServerAndOneResource(
                getRandomString(),
                getRandomString(),
                getRandomString()
        );

        tester = new ActionsTester(helper) {
            @Override
            BroadcastInformation newAction(String userName, int id, String description) {
                return gateway.newActionOnAppSever(userName, id, description);
            }

            @Override
            int persistedId() {
                return helper.anyServerId(environment);
            }

            @Override
            ActionsInfo actionsInRange(int id, Range<Instant> range) {
                return gateway.getActionsOnServer(id, range);
            }
        };
    }

    @Test
    public void testDoesntHaveBeforeMiddle() throws Exception {
        tester.testDoesntHaveBeforeMiddle();
    }

    @Test
    public void testDoesntHaveAfter() throws Exception {
        tester.testDoesntHaveAfter();
    }

    @Test
    public void testReturnsSubscribersNamesAfterAction() throws Exception {
        tester.testReturnsSubscribersNamesAfterAction(environment);
    }

    @Test(expected = NoSuchServer.class)
    public void testNoSuchServerWhenGetActionsOfNotPersistedEntity() {
        tester.testNoSuchEntityWhenGetActionsOfNotPersistedEntity();
    }

    @Test(expected = EmptyDescription.class)
    public void testEmptyDescriptionWhenNoActionDataProvided() {
        tester.testEmptyDescriptionWhenNoActionDataProvided();
    }

    @Test(expected = NoSuchUser.class)
    public void testNoSuchUserWhenNewActionWithNotPresentUser() {
        tester.testNoSuchUserWhenNewActionWithNotPresentUser();
    }
}
