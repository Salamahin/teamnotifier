package com.home.teamnotifier.db;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.home.teamnotifier.core.BroadcastInformation;
import com.home.teamnotifier.core.responses.notification.EventType;
import com.home.teamnotifier.core.responses.notification.NotificationInfo;
import com.home.teamnotifier.gateways.*;
import org.hibernate.exception.ConstraintViolationException;

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
    private final TransactionHelper transactionHelper;

    @Inject
    public DbSubscriptionGateway(final TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }

    @Override
    public BroadcastInformation subscribe(final String userName, final int serverId) {
        try {
            return transactionHelper.transaction(em -> trySubscribe(userName, serverId, em));
        } catch (Exception exc) {
            rethrowConstraintViolation(exc, userName, serverId);
            return null;
        }
    }

    private void rethrowConstraintViolation(Exception exc, String userName, int serverId) {
        final Optional<Throwable> firstConstraintViolation = Throwables.getCausalChain(exc).stream()
                .filter(ConstraintViolationException.class::isInstance)
                .findFirst();

        if (firstConstraintViolation.isPresent())
            throw new AlreadySubscribed(
                    String.format(
                            "User %s is already subscribed on server id %d",
                            userName,
                            serverId
                    ),
                    firstConstraintViolation.get()
            );
        else
            Throwables.propagate(exc);
    }

    private BroadcastInformation trySubscribe(String userName, int serverId, EntityManager em) {
        final UserEntity userEntity = getUserEntity(userName, em);
        final AppServerEntity appServerEntity = getAppServerEntity(serverId, em);

        final SubscriptionEntity subscriptionEntity = em
                .merge(new SubscriptionEntity(appServerEntity, userEntity));

        final List<String> subscribersNames = getSubscribersButUser(userName, appServerEntity);
        return new BroadcastInformation(
                new NotificationInfo(
                        userName,
                        subscriptionEntity.getTimestamp(),
                        EventType.SUBSCRIBE,
                        appServerEntity.getId(),
                        ""),
                subscribersNames
        );
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
            final Root<SubscriptionEntity> _subscription = delete.from(SubscriptionEntity.class);
            final Predicate userAndServerEqualToProvided =
                    cb.and(cb.equal(_subscription.get("subscriber"), userEntity),
                            cb.equal(_subscription.get("appServer"), serverEntity));

            final int rowsAffected = em.createQuery(delete.where(userAndServerEqualToProvided)).executeUpdate();

            if (rowsAffected == 0)
                throw new NotSubscribed(String.format("User %s was not subscribed on server %d", userName, serverId));

            final List<String> subscribersNames = getSubscribersButUser(userName, serverEntity);
            return new BroadcastInformation(
                    new NotificationInfo(
                            userName,
                            Instant.now(),
                            EventType.UNSUBSCRIBE,
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
                        EventType.RESERVE,
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
                        EventType.FREE,
                        resource.getId(),
                        ""),
                getSubscribersButUser(userName, resource.getAppServer())
        );
    }

    private SharedResourceEntity tryFree(final String userName, final int applicationId) {
        return transactionHelper.transaction(em -> {
            getUserEntity(userName, em);

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
