package com.home.teamnotifier.gateways;

import com.home.teamnotifier.db.AppServerEntity;
import com.home.teamnotifier.db.EnvironmentEntity;
import com.home.teamnotifier.db.TransactionHelper;
import com.home.teamnotifier.db.UserEntity;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

/**
 * Created by dgoloshc on 05.11.2015.
 */
public class DbSubscriptionGatewayTest
{
  private TransactionHelper helper;
  private EnvironmentEntity environmentEntity;
  private UserEntity userEntity;
  private DbSubscriptionGateway subscripbtion;

  @Before
  public void setUp() throws Exception
  {
    helper=new TransactionHelper();
    environmentEntity=createPersistedEnvironmentWithRandomName();
    userEntity=createPersistedUserWithRandomName();
    subscripbtion= new DbSubscriptionGateway(helper);
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
    return entity;
  }

  private String getRandomString()
  {
    return UUID.randomUUID().toString();
  }

  @Test
  public void testSubscribe() throws Exception
  {
    subscripbtion.subscribe(userEntity.getName(), environmentEntity.getAppServers().get(0).getId());
  }

  @Test
  public void testUnsubscribe() throws Exception
  {

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