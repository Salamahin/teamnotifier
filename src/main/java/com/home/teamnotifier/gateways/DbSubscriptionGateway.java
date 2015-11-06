package com.home.teamnotifier.gateways;

import com.google.inject.Inject;
import com.home.teamnotifier.db.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Optional;

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
  public void subscribe(final String userName, final int serverId)
  {
    transactionHelper.transaction(em -> {
      final UserEntity userEntity=getUserEntity(userName, em);
      final AppServerEntity appServerEntity=getAppServerEntity(serverId, em);

      appServerEntity.subscribe(userEntity);

      em.merge(appServerEntity);

      return null;
    });
  }

  private UserEntity getUserEntity(final String userName, final EntityManager em)
  {
    final CriteriaBuilder cb=em.getCriteriaBuilder();
    final CriteriaQuery<UserEntity> cq=cb.createQuery(UserEntity.class);
    final Root<UserEntity> rootEntry=cq.from(UserEntity.class);
    final CriteriaQuery<UserEntity> selectUserQuery=cq
        .where(cb.equal(rootEntry.get("name"), userName));

    return em.createQuery(selectUserQuery).getSingleResult();
  }

  private AppServerEntity getAppServerEntity(int serverId, EntityManager em)
  {
    return em.find(AppServerEntity.class, serverId);
  }

  @Override
  public void unsubscribe(final String userName, final int serverId)
  {
    transactionHelper.transaction(em -> {
      final AppServerEntity serverEntity=getAppServerEntity(serverId, em);
      final UserEntity userEntity=getUserEntity(userName, em);

      serverEntity.unsubscribe(userEntity);

      em.merge(serverEntity);

      return null;
    });
  }

  @Override
  public void reserve(final String userName, final int applicationId)
      throws AlreadyReserved
  {
    final boolean success=reservationSuccess(userName, applicationId);
    if (!success)
    {
      throw new AlreadyReserved();
    }
  }

  private Boolean reservationSuccess(final String userName, final int applicationId)
  {
    return transactionHelper.transaction(em -> {
      final SharedResourceEntity resourceEntity=em.find(SharedResourceEntity.class, applicationId);

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
        return false;
      }

      final UserEntity newOccupier=getUserEntity(userName, em);
      resourceEntity.reserve(newOccupier);

      em.merge(resourceEntity);

      return true;
    });
  }

  @Override
  public void free(final String userName, final int applicationId)
      throws NotReserved
  {
    if (!freeSuccess(userName, applicationId))
    {
      throw new NotReserved();
    }
  }

  private boolean freeSuccess(final String userName, final int applicationId)
  {
    return transactionHelper.transaction(em -> {
      final SharedResourceEntity resourceEntity=em.find(SharedResourceEntity.class, applicationId);
      final Optional<ReservationData> reservationData=resourceEntity.getReservationData();

      if (!reservationData.isPresent())
      {
        LOG.debug("Failed to free resource {}:{}:{} by {}: not reserved",
            resourceEntity.getAppServer().getEnvironment().getName(),
            resourceEntity.getAppServer().getName(),
            resourceEntity.getName(),
            userName
        );
        return false;
      }

      resourceEntity.free();
      em.merge(resourceEntity);

      return true;
    });
  }
}
