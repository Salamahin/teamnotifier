package com.home.teamnotifier.db;

import com.home.teamnotifier.TestHelper;
import com.home.teamnotifier.gateways.*;
import org.junit.Before;
import org.junit.Test;

import static com.home.teamnotifier.TestHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

public class DbSubscriptionGatewayTest
{
  private static final TestHelper helper = new TestHelper();

  private EnvironmentEntity environmentEntity;
  private UserEntity userEntity;
  private DbSubscriptionGateway subscripbtion;

  @Before
  public void setUp()
      throws Exception
  {
    userEntity= helper.createPersistedUser(getRandomString(), getRandomString());
    environmentEntity= helper.createPersistedEnvironmentWithOneServerAndOneResource(
        getRandomString(),
        getRandomString(),
        getRandomString()
    );
    subscripbtion=new DbSubscriptionGateway(helper.TRANSACTION_HELPER);
  }

  @Test
  public void testReserve()
      throws Exception
  {
    final Integer resourceId=environmentEntity.getImmutableListOfAppServers().get(0).getImmutableListOfResources().get(0).getId();
    subscripbtion.reserve(userEntity.getName(), resourceId);
  }

  @Test(expected = Exception.class)
  public void testDoubleSubscribeCausesException() {
    final AppServerEntity serverEntity=environmentEntity.getImmutableListOfAppServers().get(0);
    final Integer serverId=serverEntity.getId();
    final String userName=userEntity.getName();

    subscripbtion.subscribe(userName, serverId);
    subscripbtion.subscribe(userName, serverId);
  }

  @Test(expected=AlreadyReserved.class)
  public void testDoubleReserveCausesException()
      throws Exception
  {
    final String userName=userEntity.getName();
    final Integer resourceId=environmentEntity.getImmutableListOfAppServers().get(0).getImmutableListOfResources().get(0)
        .getId();

    subscripbtion.reserve(userName, resourceId);
    subscripbtion.reserve(userName, resourceId);
  }

  @Test
  public void testFree()
      throws Exception
  {
    final String userName=userEntity.getName();
    final Integer resourceId=environmentEntity.getImmutableListOfAppServers().get(0).getImmutableListOfResources().get(0)
        .getId();

    subscripbtion.reserve(userName, resourceId);
    subscripbtion.free(userName, resourceId);
  }

  @Test(expected=NotReserved.class)
  public void testFreeNotReservedResourceCausesException()
      throws Exception
  {
    final String userName=userEntity.getName();
    final Integer resourceId=environmentEntity.getImmutableListOfAppServers().get(0).getImmutableListOfResources().get(0)
        .getId();

    subscripbtion.free(userName, resourceId);
  }

  @Test
  public void testReturnsSubscribersNamesAfterSubscribe() throws Exception
  {
    final String userName1= getPersistedUserName();
    final String userName2= getPersistedUserName();

    final Integer serverId=environmentEntity.getImmutableListOfAppServers().get(0).getId();

    assertThat(subscripbtion.subscribe(userName1, serverId).getSubscribers()).doesNotContain(userName1);
    assertThat(subscripbtion.subscribe(userName2, serverId).getSubscribers()).doesNotContain(userName2).contains(userName1);
  }

  private String getPersistedUserName() {
    return helper.createPersistedUser(getRandomString(), getRandomString()).getName();
  }

  @Test
  public void testReturnsSubscribersNamesAfterUnsubscribe() throws Exception
  {
    final String userName1= getPersistedUserName();
    final String userName2= getPersistedUserName();

    final Integer serverId=environmentEntity.getImmutableListOfAppServers().get(0).getId();

    subscripbtion.subscribe(userName1, serverId);
    subscripbtion.subscribe(userName2, serverId);

    assertThat(subscripbtion.unsubscribe(userName1, serverId).getSubscribers()).doesNotContain(userName1).contains(userName2);
    assertThat(subscripbtion.unsubscribe(userName2, serverId).getSubscribers()).doesNotContain(userName2).doesNotContain(userName1);
  }

  @Test
  public void testReturnsSubscribersNamesAfterReserve() throws Exception
  {
    final String subscriber= getPersistedUserName();
    final Integer serverId=environmentEntity.getImmutableListOfAppServers().get(0).getId();
    final Integer resourceId=environmentEntity.getImmutableListOfAppServers().get(0).getImmutableListOfResources().get(0).getId();

    subscripbtion.subscribe(subscriber, serverId);

    assertThat(subscripbtion.reserve(userEntity.getName(), resourceId).getSubscribers()).contains(subscriber);
  }

  @Test
  public void testReturnsSubscribersNamesAfterFree() throws Exception
  {
    final String subscriber= getPersistedUserName();
    final Integer serverId=environmentEntity.getImmutableListOfAppServers().get(0).getId();
    final Integer resourceId=environmentEntity.getImmutableListOfAppServers().get(0).getImmutableListOfResources().get(0).getId();

    subscripbtion.subscribe(subscriber, serverId);
    subscripbtion.reserve(userEntity.getName(), resourceId);

    assertThat(subscripbtion.free(userEntity.getName(), resourceId).getSubscribers()).contains(subscriber);
  }


}