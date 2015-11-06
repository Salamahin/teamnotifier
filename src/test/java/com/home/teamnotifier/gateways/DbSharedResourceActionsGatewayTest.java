package com.home.teamnotifier.gateways;

import com.google.common.collect.Range;
import com.home.teamnotifier.db.EnvironmentEntity;
import com.home.teamnotifier.db.UserEntity;
import com.home.teamnotifier.resource.environment.ActionsInfo;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.home.teamnotifier.gateways.Commons.*;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by dgoloshc on 06.11.2015.
 */
public class DbSharedResourceActionsGatewayTest
{
  private DbSharedResourceActionsGateway gateway;
  private UserEntity user;
  private EnvironmentEntity environment;
  private LocalDateTime firstEver;
  private LocalDateTime lastEver;
  private LocalDateTime middle;
  private List<ActionData> allActionsEver;

  @Before
  public void setUp() throws Exception
  {
    gateway=new DbSharedResourceActionsGateway(HELPER);
    user=createPersistedUserWithRandomPassHash(getRandomString());
    environment=createPersistedEnvironmentWithOneServerAndOneResource(getRandomString(), getRandomString(), getRandomString());
    final Integer resourceId=environment.getImmutableListOfAppServers().get(0).getImmutableListOfResources().get(0).getId();

    for (int i=0; i < 10; i++)
    {
      gateway.newAction(user.getName(), resourceId, getRandomString());
    }

    final ActionsInfo allActionsEver=getAllActionsEver();
    this.allActionsEver=toActionDataList(allActionsEver);

    firstEver=this.allActionsEver.get(0).time;
    lastEver=this.allActionsEver.get(this.allActionsEver.size() - 1).time;

    middle=LocalDateTime.from(Duration.between(firstEver, lastEver).dividedBy(2).addTo(firstEver));
  }

  private List<ActionData> toActionDataList(ActionsInfo allActionsEver)
  {
    return allActionsEver.getActions().stream()
        .map(a -> new ActionData(a.getTimestamp(), a.getDescription()))
        .sorted((o1, o2) -> o1.time.compareTo(o2.time))
        .collect(toList());
  }

  @Test
  public void testDoesntHaveBeforeMiddle() throws Exception
  {
    final ActionsInfo actions=gateway.getActions(Range.closed(firstEver, middle));
    final List<ActionData> loadedData=toActionDataList(actions);

    final List<ActionData> dataNotInRange=new ArrayList<>(allActionsEver);
    dataNotInRange.removeAll(loadedData);

    assertThat(allActionsEver).containsAll(loadedData);
    assertThat(loadedData).doesNotContainAnyElementsOf(dataNotInRange);
  }

  @Test
  public void testDoesntHaveAfter() throws Exception
  {
    final ActionsInfo actions=gateway.getActions(Range.closed(middle, lastEver));
    final List<ActionData> loadedData=toActionDataList(actions);

    final List<ActionData> dataNotInRange=new ArrayList<>(allActionsEver);
    dataNotInRange.removeAll(loadedData);

    assertThat(allActionsEver).containsAll(loadedData);
    assertThat(loadedData).doesNotContainAnyElementsOf(dataNotInRange);
  }

  static class ActionData
  {
    public final LocalDateTime time;
    public final String description;

    ActionData(String time, String description)
    {
      this.time=LocalDateTime.parse(time);
      this.description=description;
    }

    @Override public boolean equals(Object o)
    {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      ActionData that=(ActionData) o;
      return Objects.equals(time, that.time) &&
          Objects.equals(description, that.description);
    }

    @Override public int hashCode()
    {
      return Objects.hash(time, description);
    }
  }

  private ActionsInfo getAllActionsEver()
  {
    return gateway.getActions(Range.all());
  }
}