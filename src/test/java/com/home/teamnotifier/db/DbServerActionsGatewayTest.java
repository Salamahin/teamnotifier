package com.home.teamnotifier.db;

import com.google.common.collect.Range;
import com.home.teamnotifier.core.BroadcastInformation;
import com.home.teamnotifier.core.responses.action.ServerActionsHistory;
import com.home.teamnotifier.core.responses.notification.ServerAction;
import com.home.teamnotifier.gateways.SubscriptionGateway;
import com.home.teamnotifier.gateways.UserGateway;
import com.home.teamnotifier.gateways.exceptions.EmptyDescription;
import com.home.teamnotifier.gateways.exceptions.NoSuchServer;
import com.home.teamnotifier.gateways.exceptions.NoSuchUser;
import org.dbunit.dataset.IDataSet;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class DbServerActionsGatewayTest {
    private ActionsTester<ServerActionsHistory, ServerAction> tester;
    private DbPreparer preparer;

    @Before
    public void setUp() throws Exception {
        preparer = new DbPreparer();
        preparer.initDataBase();
        initTester();
    }

    private void initTester() {
        final DbActionsGateway actionsGateway = new DbActionsGateway(preparer.getTransactionHelper());

        tester = new ActionsTester<ServerActionsHistory, ServerAction>(preparer.persistedUserName(), preparer.persistedServerId()) {
            @Override
            BroadcastInformation<ServerAction> newAction(String userName, int id, String description) {
                return actionsGateway.newServerAction(userName, id, description);
            }

            @Override
            ServerActionsHistory actionsInRange(int id, Range<Instant> range) {
                return actionsGateway.getActionsOnServer(id, range);
            }
        };
    }

    @Test
    public void testLoadByDate() throws Exception {
        final Range<Instant> timeDiapason = Range.closed(Instant.now(), Instant.now().plus(1, ChronoUnit.HOURS));

        final ActionRawDataProvider rawDataProvider = new ActionRawDataProvider("ServerAction", "server_id");
        final IDataSet dataSet = rawDataProvider.newActionPerHour(preparer.persistedUserId(), preparer.persistedServerId(), timeDiapason);
        preparer.insertMoreData(dataSet);

        tester.testLoadByDate(timeDiapason);
    }

    @Test
    public void testReturnsSubscribersNamesAfterAction() throws Exception {
        final UserGateway userGateway = new DbUserGateway(preparer.getTransactionHelper());
        final SubscriptionGateway subscriptionGateway = new DbSubscriptionGateway(preparer.getTransactionHelper());

        tester.testReturnsSubscribersNamesAfterAction(preparer.persistedServerId(), userGateway, subscriptionGateway);
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
