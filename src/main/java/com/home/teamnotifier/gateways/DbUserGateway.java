package com.home.teamnotifier.gateways;

import com.google.inject.Inject;
import com.home.teamnotifier.db.TransactionHelper;
import com.home.teamnotifier.db.UserEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.home.teamnotifier.gateways.DbGatewayCommons.getUserEntity;

public class DbUserGateway implements UserGateway
{
  private static final Logger LOG=LoggerFactory.getLogger(DbUserGateway.class);

  private final TransactionHelper transactionHelper;

  @Inject
  public DbUserGateway(final TransactionHelper transactionHelper)
  {
    this.transactionHelper=transactionHelper;
  }

  @Override
  public UserCredentials userCredentials(final String userName)
  {
    final UserEntity entity=getEntityByName(userName);
    if (entity != null)
    {
      return new UserCredentials(entity.getName(), entity.getPassHash());
    }
    else
    {
      return null;
    }
  }

  private UserEntity getEntityByName(String name)
  {
    try
    {
      return transactionHelper.transaction(em -> getUserEntity(name, em));
    }
    catch (Exception exc)
    {
      LOG.error("Failed to get user by name", exc);
    }

    return null;
  }
}
