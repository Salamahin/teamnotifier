package com.home.teamnotifier.gateways;

import com.google.inject.Inject;
import com.home.teamnotifier.db.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static com.home.teamnotifier.gateways.DbGatewayCommons.getSubscribersButUser;
import static com.home.teamnotifier.gateways.DbGatewayCommons.getUserEntity;

public class DbSubscriptionGateway implements SubscriptionGateway
{
  private static final Logger LOG=LoggerFactory.getLogger(DbSubscriptionGateway.class);

  private final TransactionHelper transactionHelper;

  @Inject
  public DbSubscriptionGateway(final TransactionHelper transactionHelper)
  {
    this.transactionHelper=transactionHelper;
  }

  @Override
  public BroadcastInformation subscribe(final String userName, final int serverId)
  {
    final AppServerEntity serverEntity=transactionHelper.transaction(em -> {
      final UserEntity userEntity=getUserEntity(userName, em);
      final AppServerEntity appServerEntity=getAppServerEntity(serverId, em);

      appServerEntity.subscribe(userEntity);

      return em.merge(appServerEntity);
    });

    return getBroadcastInformation(userName, serverEntity, String.format("%s subscribed on %s", userName, serverEntity.getName()));
  }

  private BroadcastInformation getBroadcastInformation(final String userName, final AppServerEntity serverEntity, final String message)
  {
    final List<String> subscribersNames=getSubscribersButUser(userName, serverEntity);
    return new BroadcastInformation(message, subscribersNames);
  }

  private AppServerEntity getAppServerEntity(int serverId, EntityManager em)
  {
    return em.find(AppServerEntity.class, serverId);
  }

  @Override
  public BroadcastInformation unsubscribe(final String userName, final int serverId)
  {
    return transactionHelper.transaction(em -> {
      final UserEntity userEntity=getUserEntity(userName, em);
      AppServerEntity serverEntity=getAppServerEntity(serverId, em);

      serverEntity.unsubscribe(userEntity);
      serverEntity=em.merge(serverEntity);

      return getBroadcastInformation(userName, serverEntity, String.format("%s unsubscribed from %s", userName, serverEntity.getName()));
    });
  }

  @Override
  public BroadcastInformation reserve(final String userName, final int applicationId)
      throws AlreadyReserved
  {
    final SharedResourceEntity resource=tryReserve(userName, applicationId);
    return getBroadcastInformation(userName, resource.getAppServer(), String.format(
        "%s reserved %s:%s:%s",
        userName,
        resource.getAppServer().getEnvironment().getName(),
        resource.getAppServer().getName(),
        resource.getName()
    ));
  }

  private SharedResourceEntity tryReserve(final String userName, final int applicationId)
  {
    return transactionHelper.transaction(em -> {
      SharedResourceEntity resourceEntity=em.find(SharedResourceEntity.class, applicationId);

      final Optional<ReservationData> reservationData=resourceEntity.getReservationData();

      if (reservationData.isPresent())
      {
        LOG.debug("Failed to reserve resource {}:{}:{} by {}: already reserved by {}",
            resourceEntity.getAppServer().getEnvironment().getName(),
            resourceEntity.getAppServer().getName(),
            resourceEntity.getName(),
            userName,
            reservationData.get().getOccupier().getName()
        );
        throw new AlreadyReserved();
      }

      final UserEntity newOccupier=getUserEntity(userName, em);
      resourceEntity.reserve(newOccupier);

      resourceEntity=em.merge(resourceEntity);
      return resourceEntity;
    });
  }

  @Override
  public BroadcastInformation free(final String userName, final int applicationId)
      throws NotReserved
  {
    final SharedResourceEntity resource=tryFree(userName, applicationId);
    return getBroadcastInformation(userName, resource.getAppServer(), String.format(
        "%s free %s:%s:%s",
        userName,
        resource.getAppServer().getEnvironment().getName(),
        resource.getAppServer().getName(),
        resource.getName()
    ));
  }

  private SharedResourceEntity tryFree(final String userName, final int applicationId)
  {
    return transactionHelper.transaction(em -> {
      SharedResourceEntity resourceEntity=em.find(SharedResourceEntity.class, applicationId);
      final Optional<ReservationData> reservationData=resourceEntity.getReservationData();

      if (!reservationData.isPresent())
      {
        LOG.debug("Failed to free resource {}:{}:{} by {}: not reserved",
            resourceEntity.getAppServer().getEnvironment().getName(),
            resourceEntity.getAppServer().getName(),
            resourceEntity.getName(),
            userName
        );
        throw new NotReserved();
      }

      resourceEntity.free();
      resourceEntity=em.merge(resourceEntity);

      return resourceEntity;
    });
  }
}
