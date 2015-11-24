package com.home.teamnotifier.db;

import com.google.inject.Inject;
import com.home.teamnotifier.core.responses.notification.BroadcastAction;
import com.home.teamnotifier.core.responses.notification.NotificationInfo;
import com.home.teamnotifier.gateways.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.home.teamnotifier.db.DbGatewayCommons.getSubscribersButUser;
import static com.home.teamnotifier.db.DbGatewayCommons.getUserEntity;

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
                            subscriptionEntity.getTimestamp(),
                            BroadcastAction.SUBSCRIBE,
                            appServerEntity.getId(),
                            ""),
                    subscribersNames
            );
        });
    }

    private AppServerEntity getAppServerEntity(final int serverId, final EntityManager em) {
        final AppServerEntity serverEntity = em.find(AppServerEntity.class, serverId);
        if (serverEntity == null)
            throw new NoSuchServer(String.format("No server with id %d", serverId));
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
                            Instant.now(),
                            BroadcastAction.UNSUBSCRIBE,
                            serverEntity.getId(),
                            ""),
                    subscribersNames
            );
        });
    }

    @Override
    public BroadcastInformation reserve(final String userName, final int applicationId) throws AlreadyReserved {
        final SharedResourceEntity resource = tryReserve(userName, applicationId);
        return new BroadcastInformation(
                new NotificationInfo(
                        userName,
                        resource.getReservationData().get().getOccupationTime(),
                        BroadcastAction.RESERVE,
                        resource.getId(),
                        ""),
                getSubscribersButUser(userName, resource.getAppServer())
        );
    }

    private SharedResourceEntity tryReserve(final String userName, final int applicationId) {
        return transactionHelper.transaction(em -> {
            SharedResourceEntity resourceEntity = getSharedResourceEntity(applicationId, em);

            final Optional<ReservationData> reservationData = resourceEntity.getReservationData();

            if (reservationData.isPresent()) {
                throw new AlreadyReserved(String.format(
                        "Resource %d already reserved by user %s",
                        applicationId,
                        reservationData.get().getOccupier().getName()
                ));
            }

            final UserEntity newOccupier = getUserEntity(userName, em);
            resourceEntity.reserve(newOccupier);

            resourceEntity = em.merge(resourceEntity);
            return resourceEntity;
        });
    }

    private SharedResourceEntity getSharedResourceEntity(final int applicationId, final EntityManager em) {
        final SharedResourceEntity entity = em.find(SharedResourceEntity.class, applicationId);
        if (entity == null)
            throw new NoSuchResource(String.format("No resource with id %d", applicationId));
        return entity;
    }

    @Override
    public BroadcastInformation free(final String userName, final int applicationId) throws NotReserved {
        final SharedResourceEntity resource = tryFree(userName, applicationId);
        return new BroadcastInformation(
                new NotificationInfo(
                        userName,
                        Instant.now(),
                        BroadcastAction.FREE,
                        resource.getId(),
                        ""),
                getSubscribersButUser(userName, resource.getAppServer())
        );
    }

    private SharedResourceEntity tryFree(final String userName, final int applicationId) {
        return transactionHelper.transaction(em -> {
            final UserEntity userEntity = getUserEntity(userName, em);

            SharedResourceEntity resourceEntity = getSharedResourceEntity(applicationId, em);
            final Optional<ReservationData> reservationData = resourceEntity.getReservationData();

            if (!reservationData.isPresent()) {
                throw new NotReserved(String.format("Resource id %d is not reserved", applicationId));
            }

            final String occupierName = reservationData
                    .map(ReservationData::getOccupier)
                    .map(UserEntity::getName)
                    .get();

            if (!Objects.equals(userName, occupierName))
                throw new ReservedByDifferentUser(String.format(
                        "Resource %d was reserved by user %s",
                        applicationId,
                        userName
                ));

            resourceEntity.free();
            resourceEntity = em.merge(resourceEntity);

            return resourceEntity;
        });
    }
}
