package com.home.teamnotifier.db;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.home.teamnotifier.DbPreparer;
import com.home.teamnotifier.core.BroadcastInformation;
import com.home.teamnotifier.core.responses.action.AbstractActionsInfo;
import com.home.teamnotifier.core.responses.notification.DescribedUserNotification;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import static com.home.teamnotifier.DbPreparer.getRandomString;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

abstract class ActionsTester<T extends AbstractActionsInfo, K extends DescribedUserNotification> {
    private final DbPreparer helper;

    private Instant firstEver;
    private Instant lastEver;
    private Instant middle;
    private List<ActionData> actionsBeforeMiddle;
    private List<ActionData> actionAfterMiddle;
    private final int id;
    private final UserEntity persistedUser;

    public ActionsTester(final DbPreparer helper) {
        this.helper = helper;

        persistedUser = helper.createPersistedUser(getRandomString(), getRandomString());

        id = persistedId();
        for (int i = 0; i < 10; i++) {
            newAction(persistedUser.getName(), id, getRandomString());
        }

        final List<ActionData> actionsEver = toActionDataList(actionsInRange(id, Range.all()));

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

    abstract BroadcastInformation<K> newAction(final String userName, final int id, final String description);

    abstract int persistedId();

    abstract T actionsInRange(final int id, final Range<Instant> range);

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

    private List<ActionData> toActionDataList(T allActionsEver) {
        return ImmutableList.copyOf(
                allActionsEver.getActions().stream()
                        .map(a -> new ActionData(a.getTimestamp(), a.getDescription()))
                        .sorted((o1, o2) -> o1.time.compareTo(o2.time))
                        .collect(toList())
        );
    }

    public void testDoesntHaveBeforeMiddle() throws Exception {
        final T actions = actionsInRange(id, Range.closed(firstEver, middle));
        final List<ActionData> loadedData = toActionDataList(actions);

        assertThat(loadedData).containsAll(actionsBeforeMiddle);
        assertThat(loadedData).doesNotContainAnyElementsOf(actionAfterMiddle);
    }


    public void testDoesntHaveAfter() throws Exception {
        final T actions =actionsInRange(id, Range.closed(middle, lastEver));
        final List<ActionData> loadedData = toActionDataList(actions);

        assertThat(loadedData).containsAll(actionAfterMiddle);
        assertThat(loadedData).doesNotContainAnyElementsOf(actionsBeforeMiddle);
    }


    public void testReturnsSubscribersNamesAfterAction(final EnvironmentEntity environment) throws Exception {
        final String userName1 = persistedUser.getName();
        final String userName2 = helper.createPersistedUser(getRandomString(), getRandomString()).getName();

        final Integer serverId = helper.anyServerId(environment);

        final DbSubscriptionGateway subscription = new DbSubscriptionGateway(helper.TRANSACTION_HELPER);
        subscription.subscribe(userName1, serverId);
        subscription.subscribe(userName2, serverId);

        assertThat(newAction(userName1, id, getRandomString()).getSubscribers())
                .doesNotContain(userName1)
                .contains(userName2);
    }

    public void testNoSuchEntityWhenGetActionsOfNotPersistedEntity() {
        final String userName = persistedUser.getName();
        newAction(userName, -1, "action");
    }

    public void testEmptyDescriptionWhenNoActionDataProvided() {
        final String userName = persistedUser.getName();
        newAction(userName, id, "");
    }

    public void testNoSuchUserWhenNewActionWithNotPresentUser() {
        newAction(getRandomString(), id, "action");
    }
}
