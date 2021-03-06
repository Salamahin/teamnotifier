package com.home.teamnotifier.db;

import com.google.common.collect.Range;
import com.home.teamnotifier.core.BroadcastInformation;
import com.home.teamnotifier.core.responses.action.ResourceActionsHistory;
import com.home.teamnotifier.core.responses.notification.ResourceAction;
import com.home.teamnotifier.gateways.ResourceDescription;
import com.home.teamnotifier.gateways.SubscriptionGateway;
import com.home.teamnotifier.gateways.UserGateway;
import com.home.teamnotifier.gateways.exceptions.EmptyDescription;
import com.home.teamnotifier.gateways.exceptions.NoSuchResource;
import com.home.teamnotifier.gateways.exceptions.NoSuchUser;
import com.sun.org.apache.regexp.internal.RESyntaxException;
import org.dbunit.dataset.IDataSet;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.home.teamnotifier.db.tools.MockedCheckerProvider.getMockedChecker;
import static org.assertj.core.api.Assertions.assertThat;

public class DbResourceActionsGatewayTest {
    private ActionsTester<ResourceActionsHistory, ResourceAction> tester;
    private DbPreparer preparer;
    private DbActionsGateway actionsGateway;

    @Before
    public void setUp() throws Exception {
        preparer = new DbPreparer();
        preparer.initDataBase();
        actionsGateway = new DbActionsGateway(preparer.getTransactionHelper());
        initTester();
    }

    private void initTester() {
        tester = new ActionsTester<ResourceActionsHistory, ResourceAction>(preparer.persistedUserName(), preparer.persistedResourceId()) {
            @Override
            BroadcastInformation<ResourceAction> newAction(String userName, int id, String description) {
                return actionsGateway.newResourceAction(userName, id, description);
            }

            @Override
            ResourceActionsHistory actionsInRange(int id, Range<Instant> range) {
                return actionsGateway.getActionsOnResource(id, range);
            }
        };
    }

    @Test
    public void testActionByResourceDescription() throws Exception {
        final BroadcastInformation<ResourceAction> action = actionsGateway.newResourceAction(
                preparer.persistedUserName(),
                preparer.getPersistedResourceDescription(),
                "test"
        );

        assertThat(action.getValue()).isNotNull();
    }

    @Test
    public void testLoadByDate() throws Exception {
        final Range<Instant> timeDiapason = Range.closed(Instant.now(), Instant.now().plus(1, ChronoUnit.HOURS));

        final ActionRawDataProvider rawDataProvider = new ActionRawDataProvider("ResourceAction", "resource_id");
        final IDataSet dataSet = rawDataProvider.newActionPerHour(preparer.persistedUserId(), preparer.persistedResourceId(), timeDiapason);
        preparer.insertMoreData(dataSet);

        tester.testLoadByDate(timeDiapason);
    }

    @Test
    public void testReturnsSubscribersNamesAfterAction() throws Exception {
        final UserGateway userGateway = new DbUserGateway(preparer.getTransactionHelper());
        final SubscriptionGateway subscriptionGateway = new DbSubscriptionGateway(
                preparer.getTransactionHelper(),
                getMockedChecker()
        );

        tester.testReturnsSubscribersNamesAfterAction(preparer.persistedServerId(), userGateway, subscriptionGateway);
    }

    @Test(expected = NoSuchResource.class)
    public void testNoSuchResourceWhenGetActionsOfNotPersistedEntity() {
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
