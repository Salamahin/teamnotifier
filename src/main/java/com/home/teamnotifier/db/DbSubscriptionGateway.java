package com.home.teamnotifier.db;

import com.google.inject.Inject;
import com.home.teamnotifier.gateways.*;
import org.slf4j.*;
import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.*;

public class DbSubscriptionGateway implements SubscriptionGateway {
  private static final Logger LOGGER = LoggerFactory.getLogger(DbSubscriptionGateway.class);

  private final TransactionHelper transactionHelper;

  @Inject
  public DbSubscriptionGateway(final TransactionHelper transactionHelper) {
    this.transactionHelper = transactionHelper;
  }

  @Override
  public BroadcastInformation subscribe(final String userName, final int serverId) {
    return transactionHelper.transaction(em -> {
      final UserEntity userEntity = DbGatewayCommons.getUserEntity(userName, em);
      AppServerEntity appServerEntity = getAppServerEntity(serverId, em);

      final LocalDateTime time = appServerEntity.subscribe(userEntity);

      appServerEntity = em.merge(appServerEntity);

      return getBroadcastInformation(
          userName,
          appServerEntity,
          String.format("%s subscribed on %s", userName, appServerEntity.getName()),
          time
      );
    });
  }

  private AppServerEntity getAppServerEntity(int serverId, EntityManager em) {
    return em.find(AppServerEntity.class, serverId);
  }

  private BroadcastInformation getBroadcastInformation(final String userName, final
  AppServerEntity serverEntity, final String message, final LocalDateTime time) {
    final List<String> subscribersNames = DbGatewayCommons
        .getSubscribersButUser(userName, serverEntity);
    return new BroadcastInformation(message, time, subscribersNames);
  }

  @Override
  public BroadcastInformation unsubscribe(final String userName, final int serverId) {
    return transactionHelper.transaction(em -> {
      final UserEntity userEntity = DbGatewayCommons.getUserEntity(userName, em);
      AppServerEntity serverEntity = getAppServerEntity(serverId, em);
      final LocalDateTime time = serverEntity.unsubscribe(userEntity);
      serverEntity = em.merge(serverEntity);

      return getBroadcastInformation(
          userName,
          serverEntity,
          String.format("%s unsubscribed from %s", userName, serverEntity.getName()),
          time
      );
    });
  }

  @Override
  public BroadcastInformation reserve(final String userName, final int applicationId)
  throws AlreadyReserved {
    final SharedResourceEntity resource = tryReserve(userName, applicationId);
    return getBroadcastInformation(
        userName,
        resource.getAppServer(),
        String.format(
            "%s reserved %s:%s:%s",
            userName,
            resource.getAppServer().getEnvironment().getName(),
            resource.getAppServer().getName(),
            resource.getName()
        ),
        resource.getReservationData().get().getOccupationTime()
    );
  }

  private SharedResourceEntity tryReserve(final String userName, final int applicationId) {
    return transactionHelper.transaction(em -> {
      SharedResourceEntity resourceEntity = em.find(SharedResourceEntity.class, applicationId);

      final Optional<ReservationData> reservationData = resourceEntity.getReservationData();

      if (reservationData.isPresent()) {
        LOGGER.debug("Failed to reserve resource {}:{}:{} by {}: already reserved by {}",
            resourceEntity.getAppServer().getEnvironment().getName(),
            resourceEntity.getAppServer().getName(),
            resourceEntity.getName(),
            userName,
            reservationData.get().getOccupier().getName()
        );
        throw new AlreadyReserved();
      }

      final UserEntity newOccupier = DbGatewayCommons.getUserEntity(userName, em);
      resourceEntity.reserve(newOccupier);

      resourceEntity = em.merge(resourceEntity);
      return resourceEntity;
    });
  }

  @Override
  public BroadcastInformation free(final String userName, final int applicationId)
  throws NotReserved {
    final SharedResourceEntity resource = tryFree(userName, applicationId);
    return getBroadcastInformation(
        userName,
        resource.getAppServer(),
        String.format(
            "%s free %s:%s:%s",
            userName,
            resource.getAppServer().getEnvironment().getName(),
            resource.getAppServer().getName(),
            resource.getName()),
        LocalDateTime.now()
    );
  }

  private SharedResourceEntity tryFree(final String userName, final int applicationId) {
    return transactionHelper.transaction(em -> {
      SharedResourceEntity resourceEntity = em.find(SharedResourceEntity.class, applicationId);
      final Optional<ReservationData> reservationData = resourceEntity.getReservationData();

      if (!reservationData.isPresent()) {
        LOGGER.debug("Failed to free resource {}:{}:{} by {}: not reserved",
            resourceEntity.getAppServer().getEnvironment().getName(),
            resourceEntity.getAppServer().getName(),
            resourceEntity.getName(),
            userName
        );
        throw new NotReserved();
      }

      resourceEntity.free();
      resourceEntity = em.merge(resourceEntity);

      return resourceEntity;
    });
  }
}
