package com.home.teamnotifier.db;

import com.google.common.collect.Range;
import com.home.teamnotifier.DbPreparer;
import com.home.teamnotifier.core.responses.action.ActionsInfo;
import com.home.teamnotifier.gateways.EmptyDescription;
import com.home.teamnotifier.gateways.NoSuchResource;
import com.home.teamnotifier.gateways.NoSuchUser;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static com.home.teamnotifier.DbPreparer.getRandomString;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class DbSharedResourceActionsGatewayTest {
    private static final DbPreparer helper = new DbPreparer();

    private DbSharedResourceActionsGateway gateway;
    private EnvironmentEntity environment;
    private Instant firstEver;
    private Instant lastEver;
    private Instant middle;
    private List<ActionData> actionsBeforeMiddle;
    private List<ActionData> actionAfterMiddle;
    private Integer resourceId;

    @Test
    public void testDoesntHaveBeforeMiddle()
            throws Exception {
        final ActionsInfo actions = gateway.getActions(resourceId, Range.closed(firstEver, middle));
        final List<ActionData> loadedData = toActionDataList(actions);

        assertThat(loadedData).containsAll(actionsBeforeMiddle);
        assertThat(loadedData).doesNotContainAnyElementsOf(actionAfterMiddle);
    }

    private List<ActionData> toActionDataList(ActionsInfo allActionsEver) {
        return allActionsEver.getActions().stream()
                .map(a -> new ActionData(a.getTimestamp(), a.getDescription()))
                .sorted((o1, o2) -> o1.time.compareTo(o2.time))
                .collect(toList());
    }

    @Test
    public void testDoesntHaveAfter()
            throws Exception {
        final ActionsInfo actions = gateway.getActions(resourceId, Range.closed(middle, lastEver));
        final List<ActionData> loadedData = toActionDataList(actions);

        assertThat(loadedData).containsAll(actionAfterMiddle);
        assertThat(loadedData).doesNotContainAnyElementsOf(actionsBeforeMiddle);
    }

    @Test
    public void testReturnsSubscribersNamesAfterAction()
            throws Exception {
        final String userName1 = helper.createPersistedUser(getRandomString(), getRandomString())
                .getName();
        final String userName2 = helper.createPersistedUser(getRandomString(), getRandomString())
                .getName();

        final Integer serverId = environment.getImmutableListOfAppServers().get(0).getId();
        final Integer resourceId = environment
                .getImmutableListOfAppServers().get(0)
                .getImmutableListOfResources().get(0)
                .getId();

        final DbSubscriptionGateway subscription = new DbSubscriptionGateway(helper.TRANSACTION_HELPER);
        subscription.subscribe(userName1, serverId);
        subscription.subscribe(userName2, serverId);

        assertThat(gateway.newAction(userName1, resourceId, getRandomString()).getSubscribers())
                .doesNotContain(userName1)
                .contains(userName2);
    }

    @Test(expected = NoSuchResource.class)
    public void testNoSuchResourceWhenNewActionWithNotPresentResource() {
        final String userName = helper.createPersistedUser(getRandomString(), getRandomString()).getName();
        gateway.newAction(userName, -1, "desc");
    }

    @Test(expected = NoSuchResource.class)
    public void testNoSuchResourceWhenGetActionsOfNotPersistedResource() {
        gateway.getActions(-1, Range.closed(firstEver, lastEver));
    }

    @Test(expected = EmptyDescription.class)
    public void testEmptyDescriptionWhenNewAction() {
        final String userName = helper.createPersistedUser(getRandomString(), getRandomString()).getName();
        gateway.newAction(userName, resourceId, "");
    }

    @Test(expected = NoSuchUser.class)
    public void testNoSuchUserWhenNewActionWithNotPresentUser() {
        gateway.newAction(getRandomString(), resourceId, "");
    }


    @Before
    public void setUp()
            throws Exception {
        gateway = new DbSharedResourceActionsGateway(helper.TRANSACTION_HELPER);
        final UserEntity user =
                helper.createPersistedUser(getRandomString(), getRandomString());
        environment = helper.createPersistedEnvironmentWithOneServerAndOneResource(
                getRandomString(), getRandomString(), getRandomString());
        resourceId = environment.getImmutableListOfAppServers().get(0)
                .getImmutableListOfResources().get(0).getId();

        for (int i = 0; i < 10; i++) {
            gateway.newAction(user.getName(), resourceId, getRandomString());
        }

        final ActionsInfo allActionsEver = getAllActionsEver();
        final List<ActionData> actionsEver = toActionDataList(allActionsEver);

        firstEver = actionsEver.get(0).time;
        lastEver = actionsEver.get(actionsEver.size() - 1).time;

        middle = Instant
                .from(Duration.between(firstEver, lastEver).dividedBy(2).addTo(firstEver));

        actionsBeforeMiddle = actionsEver.stream()
                .filter(ad -> ad.time.compareTo(middle) <= 0)
                .collect(toList());

        actionAfterMiddle = actionsEver.stream()
                .filter(ad -> ad.time.compareTo(middle) >= 0)
                .collect(toList());
    }

    private ActionsInfo getAllActionsEver() {
        return gateway.getActions(resourceId, Range.all());
    }

    private static class ActionData {
        public final Instant time;

        public final String description;

        ActionData(Instant time, String description) {
            this.time = time;
            this.description = description;
        }

        @Override
        public int hashCode() {
            return Objects.hash(time, description);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ActionData that = (ActionData) o;
            return Objects.equals(time, that.time) &&
                    Objects.equals(description, that.description);
        }
    }
}