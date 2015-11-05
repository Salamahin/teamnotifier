package com.home.teamnotifier.gateways;

import com.home.teamnotifier.db.*;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by dgoloshc on 05.11.2015.
 */
public class DbSubscriptionGatewayTest
{
  private TransactionHelper helper;
  private EnvironmentEntity environmentEntity;
  private UserEntity userEntity;
  private DbSubscriptionGateway subscripbtion;
  private SharedResourceEntity sharedResourceEntity;

  @Before
  public void setUp() throws Exception
  {
    helper=new TransactionHelper();
    environmentEntity=createPersistedEnvironmentWithRandomName();
    userEntity=createPersistedUserWithRandomName();
    subscripbtion=new DbSubscriptionGateway(helper);
  }

  private UserEntity createPersistedUserWithRandomName()
  {
    final UserEntity entity=new UserEntity();
    entity.setName(getRandomString());
    entity.setPassHash(getRandomString());
    return helper.transaction(em -> em.merge(entity));
  }

  private EnvironmentEntity createPersistedEnvironmentWithRandomName()
  {
    final EnvironmentEntity entity=new EnvironmentEntity();
    entity.setName(getRandomString());
    entity.getAppServers().add(createAppServerWithRandomName(entity));
    return helper.transaction(em -> em.merge(entity));
  }

  private AppServerEntity createAppServerWithRandomName(final EnvironmentEntity environmentEntity)
  {
    final AppServerEntity entity=new AppServerEntity();
    entity.setName(getRandomString());
    entity.setEnvironment(environmentEntity);
    entity.getResources().add(createSharedResourceWithRandomName(entity));
    return entity;
  }

  private SharedResourceEntity createSharedResourceWithRandomName(final AppServerEntity appServerEntity) {
    final SharedResourceEntity entity = new SharedResourceEntity();
    entity.setAppServer(appServerEntity);
    entity.setName(getRandomString());
    return entity;
  }

  private String getRandomString()
  {
    return UUID.randomUUID().toString();
  }

  @Test
  public void testSubscribe() throws Exception
  {
    final Integer serverId=environmentEntity.getAppServers().get(0).getId();

    assertThat(subscripbtion.getSubscribers(serverId)).isEmpty();
    subscripbtion.subscribe(userEntity.getName(), serverId);
    assertThat(subscripbtion.getSubscribers(serverId)).isNotEmpty();
  }

  @Test
  public void testUnsubscribe() throws Exception
  {
    final Integer serverId=environmentEntity.getAppServers().get(0).getId();
    subscripbtion.subscribe(userEntity.getName(), serverId);
    subscripbtion.unsubscribe(userEntity.getName(), serverId);
    assertThat(subscripbtion.getSubscribers(serverId)).isEmpty();
  }

  @Test
  public void testReserve() throws Exception
  {

  }

  @Test
  public void testFree() throws Exception
  {

  }
}