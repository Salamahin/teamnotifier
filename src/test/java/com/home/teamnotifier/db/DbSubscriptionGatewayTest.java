package com.home.teamnotifier.db;

import com.home.teamnotifier.gateways.*;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DbSubscriptionGatewayTest
{

  private EnvironmentEntity environmentEntity;

  private UserEntity userEntity;

  private DbSubscriptionGateway subscripbtion;

  @Test
  public void testSubscribe()
      throws Exception
  {
    final AppServerEntity serverEntity=environmentEntity.getImmutableListOfAppServers().get(0);
    final Integer serverId=serverEntity.getId();

    assertThat(serverEntity.getImmutableListOfSubscribers()).isEmpty();
    subscripbtion.subscribe(userEntity.getName(), serverId);
    assertThat(serverEntity.getImmutableListOfSubscribers()).isNotEmpty();
  }

  @Test
  public void testUnsubscribe()
      throws Exception
  {
    final AppServerEntity serverEntity=environmentEntity.getImmutableListOfAppServers().get(0);
    final Integer serverId=serverEntity.getId();
    subscripbtion.subscribe(userEntity.getName(), serverId);
    subscripbtion.unsubscribe(userEntity.getName(), serverId);
    assertThat(serverEntity.getImmutableListOfSubscribers()).isEmpty();
  }

  @Test
  public void testReserve()
      throws Exception
  {
    final Integer resourceId=environmentEntity.getImmutableListOfAppServers().get(0).getImmutableListOfResources().get(0)
        .getId();
    subscripbtion.reserve(userEntity.getName(), resourceId);
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
    final String userName1= Commons.createPersistedUserWithRandomPassHash(Commons.getRandomString()).getName();
    final String userName2= Commons.createPersistedUserWithRandomPassHash(Commons.getRandomString()).getName();

    final Integer serverId=environmentEntity.getImmutableListOfAppServers().get(0).getId();

    assertThat(subscripbtion.subscribe(userName1, serverId).getSubscribers()).doesNotContain(userName1);
    assertThat(subscripbtion.subscribe(userName2, serverId).getSubscribers()).doesNotContain(userName2).contains(userName1);
  }

  @Test
  public void testReturnsSubscribersNamesAfterUnsubscribe() throws Exception
  {
    final String userName1= Commons.createPersistedUserWithRandomPassHash(Commons.getRandomString()).getName();
    final String userName2= Commons.createPersistedUserWithRandomPassHash(Commons.getRandomString()).getName();

    final Integer serverId=environmentEntity.getImmutableListOfAppServers().get(0).getId();

    subscripbtion.subscribe(userName1, serverId);
    subscripbtion.subscribe(userName2, serverId);

    assertThat(subscripbtion.unsubscribe(userName1, serverId).getSubscribers()).doesNotContain(userName1).contains(userName2);
    assertThat(subscripbtion.unsubscribe(userName2, serverId).getSubscribers()).doesNotContain(userName2).doesNotContain(userName1);
  }

  @Test
  public void testReturnsSubscribersNamesAfterReserve() throws Exception
  {
    final String subscriber= Commons.createPersistedUserWithRandomPassHash(Commons.getRandomString()).getName();
    final Integer serverId=environmentEntity.getImmutableListOfAppServers().get(0).getId();
    final Integer resourceId=environmentEntity.getImmutableListOfAppServers().get(0).getImmutableListOfResources().get(0).getId();

    subscripbtion.subscribe(subscriber, serverId);

    assertThat(subscripbtion.reserve(userEntity.getName(), resourceId).getSubscribers()).contains(subscriber);
  }

  @Test
  public void testReturnsSubscribersNamesAfterFree() throws Exception
  {
    final String subscriber= Commons.createPersistedUserWithRandomPassHash(Commons.getRandomString()).getName();
    final Integer serverId=environmentEntity.getImmutableListOfAppServers().get(0).getId();
    final Integer resourceId=environmentEntity.getImmutableListOfAppServers().get(0).getImmutableListOfResources().get(0).getId();

    subscripbtion.subscribe(subscriber, serverId);
    subscripbtion.reserve(userEntity.getName(), resourceId);

    assertThat(subscripbtion.free(userEntity.getName(), resourceId).getSubscribers()).contains(subscriber);
  }

  @Before
  public void setUp()
      throws Exception
  {
    userEntity= Commons.createPersistedUserWithRandomPassHash(Commons.getRandomString());
    environmentEntity= Commons.createPersistedEnvironmentWithOneServerAndOneResource(
        Commons.getRandomString(),
        Commons.getRandomString(),
        Commons.getRandomString()
    );
    subscripbtion=new DbSubscriptionGateway(Commons.HELPER);
  }
}