package com.home.teamnotifier.db;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.home.teamnotifier.core.responses.notification.BroadcastAction;
import com.home.teamnotifier.core.responses.notification.NotificationInfo;
import com.home.teamnotifier.gateways.*;
import org.slf4j.*;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.*;

import static com.home.teamnotifier.db.DbGatewayCommons.*;

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
            final UserEntity userEntity = getUserEntity(userName, em);
            final AppServerEntity appServerEntity = getAppServerEntity(serverId, em);

            final SubscriptionEntity subscriptionEntity = em
                    .merge(new SubscriptionEntity(appServerEntity, userEntity));

            final List<String> subscribersNames = getSubscribersButUser(userName, appServerEntity);
            return new BroadcastInformation(
                    new NotificationInfo(
                            userName,
                            subscriptionEntity.getTimestamp().toString(),
                            BroadcastAction.SUBSCRIBE,
                            appServerEntity.getName()
                    ),
                    subscribersNames
            );
        });
    }

    private AppServerEntity getAppServerEntity(final int serverId, final EntityManager em) {
        final AppServerEntity serverEntity = em.find(AppServerEntity.class, serverId);
        Preconditions.checkNotNull(serverEntity, "No server with provided id %s", serverId);
        return serverEntity;
    }


    @Override
    public BroadcastInformation unsubscribe(final String userName, final int serverId) {
        return transactionHelper.transaction(em -> {
            final UserEntity userEntity = getUserEntity(userName, em);
            final AppServerEntity serverEntity = getAppServerEntity(serverId, em);

            final CriteriaBuilder cb = em.getCriteriaBuilder();
            final CriteriaDelete<SubscriptionEntity> delete = cb
                    .createCriteriaDelete(SubscriptionEntity.class);
            final Root<SubscriptionEntity> _subsctiption = delete.from(SubscriptionEntity.class);
            final Predicate userAndServerEqualToProvided =
                    cb.and(cb.equal(_subsctiption.get("subscriber"), userEntity),
                            cb.equal(_subsctiption.get("appServer"), serverEntity));

            em.createQuery(delete.where(userAndServerEqualToProvided)).executeUpdate();

            final List<String> subscribersNames = getSubscribersButUser(userName, serverEntity);
            return new BroadcastInformation(
                    new NotificationInfo(
                            userName,
                            LocalDateTime.now().toString(),
                            BroadcastAction.SUBSCRIBE,
                            serverEntity.getName()
                    ),
                    subscribersNames
            );
        });
    }

    @Override
    public BroadcastInformation reserve(final String userName, final int applicationId)
            throws AlreadyReserved {
        final SharedResourceEntity resource = tryReserve(userName, applicationId);
        return new BroadcastInformation(
                new NotificationInfo(
                        userName,
                        resource.getReservationData().get().getOccupationTime().toString(),
                        BroadcastAction.RESERVE,
                        getTargetString(resource)
                ),
                getSubscribersButUser(userName, resource.getAppServer())
        );
    }

    private SharedResourceEntity tryReserve(final String userName, final int applicationId) {
        return transactionHelper.transaction(em -> {
            SharedResourceEntity resourceEntity = getSharedResourceEntity(applicationId, em);

            final Optional<ReservationData> reservationData = resourceEntity.getReservationData();

            if (reservationData.isPresent()) {
                LOGGER.debug("Failed to reserve resource {} by {}: already reserved by {}",
                        getTargetString(resourceEntity),
                        userName,
                        reservationData.get().getOccupier().getName()
                );
                throw new AlreadyReserved();
            }

            final UserEntity newOccupier = getUserEntity(userName, em);
            resourceEntity.reserve(newOccupier);

            resourceEntity = em.merge(resourceEntity);
            return resourceEntity;
        });
    }

    private SharedResourceEntity getSharedResourceEntity(final int applicationId, final EntityManager em) {
        final SharedResourceEntity entity = em.find(SharedResourceEntity.class, applicationId);
        Preconditions.checkNotNull(entity, "No resource with provided id %s", applicationId);
        return entity;
    }

    @Override
    public BroadcastInformation free(final String userName, final int applicationId)
            throws NotReserved {
        final SharedResourceEntity resource = tryFree(userName, applicationId);
        return new BroadcastInformation(
                new NotificationInfo(
                        userName,
                        LocalDateTime.now().toString(),
                        BroadcastAction.FREE,
                        getTargetString(resource)
                ),
                getSubscribersButUser(userName, resource.getAppServer())
        );
    }

    private String getTargetString(final SharedResourceEntity resourceEntity) {
        return String.format("%s : %s : %s",
                resourceEntity.getAppServer().getEnvironment().getName(),
                resourceEntity.getAppServer().getName(),
                resourceEntity.getName()
        );
    }

    private SharedResourceEntity tryFree(final String userName, final int applicationId) {
        return transactionHelper.transaction(em -> {
            SharedResourceEntity resourceEntity = getSharedResourceEntity(applicationId, em);
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
