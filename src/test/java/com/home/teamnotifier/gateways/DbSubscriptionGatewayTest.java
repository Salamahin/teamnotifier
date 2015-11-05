package com.home.teamnotifier.gateways;

import com.home.teamnotifier.db.*;
import org.junit.*;
import static com.home.teamnotifier.gateways.Commons.*;
import static org.assertj.core.api.Assertions.assertThat;

public class DbSubscriptionGatewayTest {

  private EnvironmentEntity environmentEntity;

  private UserEntity userEntity;

  private DbSubscriptionGateway subscripbtion;

  @Test
  public void testSubscribe()
  throws Exception {
    final AppServerEntity serverEntity = environmentEntity.getAppServers().get(0);
    final Integer serverId = serverEntity.getId();

    assertThat(serverEntity.getSubscriptions()).isEmpty();
    subscripbtion.subscribe(userEntity.getName(), serverId);
    assertThat(serverEntity.getSubscriptions()).isNotEmpty();
  }

  @Test
  public void testUnsubscribe()
  throws Exception {
    final AppServerEntity serverEntity = environmentEntity.getAppServers().get(0);
    final Integer serverId = serverEntity.getId();
    subscripbtion.subscribe(userEntity.getName(), serverId);
    subscripbtion.unsubscribe(userEntity.getName(), serverId);
    assertThat(serverEntity.getSubscriptions()).isEmpty();
  }

  @Test
  public void testReserve()
  throws Exception {
    final Integer resourceId = environmentEntity.getAppServers().get(0).getResources().get(0)
        .getId();
    subscripbtion.reserve(userEntity.getName(), resourceId);
  }

  @Test(expected = AlreadyReserved.class)
  public void testDoubleReserveCausesException()
  throws Exception {
    final String userName = userEntity.getName();
    final Integer resourceId = environmentEntity.getAppServers().get(0).getResources().get(0)
        .getId();

    subscripbtion.reserve(userName, resourceId);
    subscripbtion.reserve(userName, resourceId);
  }

  @Test
  public void testFree()
  throws Exception {
    final String userName = userEntity.getName();
    final Integer resourceId = environmentEntity.getAppServers().get(0).getResources().get(0)
        .getId();

    subscripbtion.reserve(userName, resourceId);
    subscripbtion.free(userName, resourceId);
  }

  @Test(expected = NotReserved.class)
  public void testFreeNotReservedResourceCausesException()
  throws Exception {
    final String userName = userEntity.getName();
    final Integer resourceId = environmentEntity.getAppServers().get(0).getResources().get(0)
        .getId();

    subscripbtion.free(userName, resourceId);
  }

  @Before
  public void setUp()
  throws Exception {
    userEntity = createPersistedUserWithRandomPassHash(getRandomString());
    environmentEntity = createPersistedEnvironmentWithOneServerAndOneResource(
        getRandomString(),
        getRandomString(),
        getRandomString()
    );
    subscripbtion = new DbSubscriptionGateway(HELPER);
  }
}