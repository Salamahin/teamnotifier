package com.home.teamnotifier.db;

import com.google.common.collect.Range;
import com.home.teamnotifier.resource.environment.*;
import org.junit.*;
import java.time.*;
import java.util.*;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class DbSharedResourceActionsGatewayTest {
  private DbSharedResourceActionsGateway gateway;

  private EnvironmentEntity environment;

  private LocalDateTime firstEver;

  private LocalDateTime lastEver;

  private LocalDateTime middle;

  private List<ActionData> allActionsEver;

  @Before
  public void setUp()
  throws Exception {
    gateway = new DbSharedResourceActionsGateway(Commons.HELPER, new LocalDateTimeFormatter());
    final UserEntity user = Commons
        .createPersistedUserWithRandomPassHash(Commons.getRandomString());
    environment = Commons.createPersistedEnvironmentWithOneServerAndOneResource(
        Commons.getRandomString(), Commons.getRandomString(), Commons.getRandomString());
    final Integer resourceId = environment.getImmutableListOfAppServers().get(0)
        .getImmutableListOfResources().get(0).getId();

    for (int i = 0; i < 10; i++) {
      gateway.newAction(user.getName(), resourceId, Commons.getRandomString());
    }

    final ActionsInfo allActionsEver = getAllActionsEver();
    this.allActionsEver = toActionDataList(allActionsEver);

    firstEver = this.allActionsEver.get(0).time;
    lastEver = this.allActionsEver.get(this.allActionsEver.size() - 1).time;

    middle = LocalDateTime
        .from(Duration.between(firstEver, lastEver).dividedBy(2).addTo(firstEver));
  }

  private List<ActionData> toActionDataList(ActionsInfo allActionsEver) {
    return allActionsEver.getActions().stream()
        .map(a -> new ActionData(a.getTimestamp(), a.getDescription()))
        .sorted((o1, o2) -> o1.time.compareTo(o2.time))
        .collect(toList());
  }

  @Test
  public void testDoesntHaveBeforeMiddle()
  throws Exception {
    final ActionsInfo actions = gateway.getActions(Range.closed(firstEver, middle));
    final List<ActionData> loadedData = toActionDataList(actions);

    final List<ActionData> dataNotInRange = new ArrayList<>(allActionsEver);
    dataNotInRange.removeAll(loadedData);

    assertThat(allActionsEver).containsAll(loadedData);
    assertThat(loadedData).doesNotContainAnyElementsOf(dataNotInRange);
  }

  @Test
  public void testDoesntHaveAfter()
  throws Exception {
    final ActionsInfo actions = gateway.getActions(Range.closed(middle, lastEver));
    final List<ActionData> loadedData = toActionDataList(actions);

    final List<ActionData> dataNotInRange = new ArrayList<>(allActionsEver);
    dataNotInRange.removeAll(loadedData);

    assertThat(allActionsEver).containsAll(loadedData);
    assertThat(loadedData).doesNotContainAnyElementsOf(dataNotInRange);
  }

  @Test
  public void testReturnsSubscribersNamesAfterAction()
  throws Exception {
    final String userName1 = Commons
        .createPersistedUserWithRandomPassHash(Commons.getRandomString()).getName();
    final String userName2 = Commons
        .createPersistedUserWithRandomPassHash(Commons.getRandomString()).getName();

    final Integer serverId = environment.getImmutableListOfAppServers().get(0).getId();
    final Integer resourceId = environment.getImmutableListOfAppServers().get(0)
        .getImmutableListOfResources().get(0).getId();

    final DbSubscriptionGateway subscription = new DbSubscriptionGateway(Commons.HELPER);
    subscription.subscribe(userName1, serverId);
    subscription.subscribe(userName2, serverId);

    assertThat(gateway.newAction(userName1, resourceId, Commons.getRandomString()).getSubscribers())
        .doesNotContain(userName1)
        .contains(userName2);
  }

  static class ActionData {
    public final LocalDateTime time;

    public final String description;

    ActionData(String time, String description) {
      this.time = LocalDateTime.parse(time);
      this.description = description;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) { return true; }
      if (o == null || getClass() != o.getClass()) { return false; }
      ActionData that = (ActionData) o;
      return Objects.equals(time, that.time) &&
          Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
      return Objects.hash(time, description);
    }
  }

  private ActionsInfo getAllActionsEver() {
    return gateway.getActions(Range.all());
  }
}