package com.home.teamnotifier.gateways;

import com.google.inject.Inject;
import com.home.teamnotifier.db.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.home.teamnotifier.gateways.DbGatewayCommons.*;
import static java.util.stream.Collectors.toList;

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
  public List<String> subscribe(final String userName, final int serverId)
  {
    final AppServerEntity serverEntity=transactionHelper.transaction(em -> {
      final UserEntity userEntity=getUserEntity(userName, em);
      final AppServerEntity appServerEntity=getAppServerEntity(serverId, em);

      appServerEntity.subscribe(userEntity);

      return em.merge(appServerEntity);
    });

    return serverEntity.getImmutableListOfSubscribers().stream()
        .map(SubscriptionData::getUserName)
        .filter(u -> !Objects.equals(u, userName))
        .collect(toList());
  }

  private AppServerEntity getAppServerEntity(int serverId, EntityManager em)
  {
    return em.find(AppServerEntity.class, serverId);
  }

  @Override
  public List<String> unsubscribe(final String userName, final int serverId)
  {
    return transactionHelper.transaction(em -> {
      final UserEntity userEntity=getUserEntity(userName, em);
      AppServerEntity serverEntity=getAppServerEntity(serverId, em);

      serverEntity.unsubscribe(userEntity);

      serverEntity=em.merge(serverEntity);

      return serverEntity.getImmutableListOfSubscribers().stream()
          .map(SubscriptionData::getUserName)
          .filter(u -> !Objects.equals(u, userName))
          .collect(toList());
    });
  }

  @Override
  public List<String> reserve(final String userName, final int applicationId)
      throws AlreadyReserved
  {
    return tryReserve(userName, applicationId).getImmutableListOfSubscribers().stream()
        .map(SubscriptionData::getUserName)
        .filter(u -> !Objects.equals(u, userName))
        .collect(toList());
  }

  private AppServerEntity tryReserve(final String userName, final int applicationId)
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
      return resourceEntity.getAppServer();
    });
  }

  @Override
  public List<String> free(final String userName, final int applicationId)
      throws NotReserved
  {
    return tryFree(userName, applicationId).getImmutableListOfSubscribers().stream()
        .map(SubscriptionData::getUserName)
        .filter(u -> !Objects.equals(u, userName))
        .collect(toList());
  }

  private AppServerEntity tryFree(final String userName, final int applicationId)
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

      return resourceEntity.getAppServer();
    });
  }
}
