package com.home.teamnotifier.db;

import com.google.common.collect.Range;
import com.home.teamnotifier.core.BroadcastInformation;
import com.home.teamnotifier.core.responses.action.AbstractActionsInfo;
import com.home.teamnotifier.core.responses.action.ActionInfo;
import com.home.teamnotifier.core.responses.notification.DescribedUserNotification;
import com.home.teamnotifier.gateways.SubscriptionGateway;
import com.home.teamnotifier.gateways.UserGateway;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

abstract class ActionsTester<T extends AbstractActionsInfo, K extends DescribedUserNotification> {

    private final String persistedUserName;
    private final int persistedTargetId;

    ActionsTester(final String persistedUserName, final int persistedTargetId) {
        this.persistedUserName = persistedUserName;
        this.persistedTargetId = persistedTargetId;
    }

    abstract BroadcastInformation<K> newAction(final String userName, final int id, final String description);

    abstract T actionsInRange(final int id, final Range<Instant> range);

    public void testLoadByDate(final Range<Instant> timeDiapason) {
        final Instant firstEver = timeDiapason.lowerEndpoint();
        final Instant lastEver = timeDiapason.upperEndpoint();

        final Instant somewhereInRange1 = Instant.from(Duration.between(lastEver, firstEver).dividedBy(3).addTo(firstEver));
        final Instant somewhereInRange2 = Instant.from(Duration.between(lastEver, firstEver).dividedBy(3).subtractFrom(lastEver));

        final T actions = actionsInRange(persistedTargetId, Range.closed(somewhereInRange1, somewhereInRange2));
        final List<ActionInfo> actionsList = actions.getActions();

        final boolean allLoadedActionsAreAfterLeftBorder = actionsList.stream()
                .allMatch(a -> a.getTimestamp().isAfter(somewhereInRange1));

        final boolean allLoadedActionsAreBeforeRightBorder = actionsList.stream()
                .allMatch(a -> a.getTimestamp().isBefore(somewhereInRange2));

        assertThat(actionsList).isNotEmpty();
        assertThat(allLoadedActionsAreAfterLeftBorder).isTrue();
        assertThat(allLoadedActionsAreBeforeRightBorder).isTrue();
    }


    public void testNoSuchEntityWhenGetActionsOfNotPersistedEntity() {
        newAction(persistedUserName, -1, "action");
    }

    public void testEmptyDescriptionWhenNoActionDataProvided() {
        newAction(persistedUserName, persistedTargetId, "");
    }

    public void testNoSuchUserWhenNewActionWithNotPresentUser() {
        newAction("invalid_login", persistedTargetId, "action");
    }

    public void testReturnsSubscribersNamesAfterAction(
            final int persistedServerId,
            final UserGateway userGateway,
            final SubscriptionGateway subscriptionGateway
    ) {
        final String userName1 = persistedUserName;
        final String userName2 = "anotherUser";

        userGateway.newUser(userName2, "password");

        subscriptionGateway.subscribe(userName1, persistedServerId);
        subscriptionGateway.subscribe(userName2, persistedServerId);

        assertThat(newAction(userName1, persistedTargetId, "new action").getSubscribers())
                .doesNotContain(userName1)
                .contains(userName2);
    }
}
