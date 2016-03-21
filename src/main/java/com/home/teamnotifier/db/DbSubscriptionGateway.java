package com.home.teamnotifier.db;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.home.teamnotifier.core.BroadcastInformation;
import com.home.teamnotifier.core.responses.notification.Reservation;
import com.home.teamnotifier.core.responses.notification.Subscription;
import com.home.teamnotifier.gateways.SubscriptionGateway;
import com.home.teamnotifier.gateways.exceptions.*;
import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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
    public BroadcastInformation<Subscription> subscribe(final String userName, final int serverId) {
        try {
            return transactionHelper.transaction(em -> {
                final UserEntity u = getUserEntity(userName, em);
                final ServerEntity s = getServerEntity(serverId, em);

                em.persist(new SubscriptionEntity(s, u));

                return new BroadcastInformation<>(Subscription.subscribe(u, s), getSubscribersButUser(u.getName(), s));
            });
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

    private ServerEntity getServerEntity(final int serverId, final EntityManager em) {
        final ServerEntity serverEntity = em.find(ServerEntity.class, serverId);
        if (serverEntity == null)
            throw new NoSuchServer(String.format("No server with id %d", serverId));
        return serverEntity;
    }


    @Override
    public BroadcastInformation<Subscription> unsubscribe(final String userName, final int serverId) {
        return transactionHelper.transaction(em -> {
            final UserEntity userEntity = getUserEntity(userName, em);
            final ServerEntity serverEntity = getServerEntity(serverId, em);

            final CriteriaBuilder cb = em.getCriteriaBuilder();
            final CriteriaDelete<SubscriptionEntity> delete = cb
                    .createCriteriaDelete(SubscriptionEntity.class);
            final Root<SubscriptionEntity> _subscription = delete.from(SubscriptionEntity.class);
            final Predicate userAndServerEqualToProvided =
                    cb.and(cb.equal(_subscription.get("subscriber"), userEntity),
                            cb.equal(_subscription.get("server"), serverEntity));

            final int rowsAffected = em.createQuery(delete.where(userAndServerEqualToProvided)).executeUpdate();

            if (rowsAffected == 0)
                throw new NotSubscribed(String.format("User %s was not subscribed on server %d", userName, serverId));

            return new BroadcastInformation<>(
                    Subscription.unsubscribe(userEntity, serverEntity),
                    getSubscribersButUser(userName, serverEntity)
            );
        });
    }

    @Override
    public BroadcastInformation<Reservation> reserve(final String userName, final int applicationId) throws AlreadyReserved {
        return transactionHelper.transaction(em -> {
            final UserEntity u = getUserEntity(userName, em);
            final ResourceEntity r = getResourceEntity(applicationId, em);

            tryReserve(u, r, em);

            return new BroadcastInformation<>(
                    Reservation.reserve(u, r),
                    getSubscribersButUser(u.getName(), r.getServer())
            );
        });
    }

    private void tryReserve(final UserEntity user, final ResourceEntity resource, EntityManager em) {

        final Optional<ReservationData> reservationData = resource.getReservationData();

        if (reservationData.isPresent()) {
            throw new AlreadyReserved(String.format(
                    "Resource %d already reserved by user %s",
                    resource.getId(),
                    reservationData.get().getOccupier().getName()
            ));
        }

        final UserEntity newOccupier = getUserEntity(user.getName(), em);
        resource.reserve(newOccupier);
        em.merge(resource);
    }

    private ResourceEntity getResourceEntity(final int applicationId, final EntityManager em) {
        final ResourceEntity entity = em.find(ResourceEntity.class, applicationId);
        if (entity == null)
            throw new NoSuchResource(String.format("No resource with id %d", applicationId));
        return entity;
    }

    @Override
    public BroadcastInformation<Reservation> free(final String userName, final int applicationId) throws NotReserved {
        return transactionHelper.transaction(em -> {
            final UserEntity u = getUserEntity(userName, em);
            final ResourceEntity s = getResourceEntity(applicationId, em);

            tryFree(u, s, em);

            return new BroadcastInformation<>(
                    Reservation.free(u, s),
                    getSubscribersButUser(u.getName(), s.getServer())
            );

        });
    }

    private void tryFree(final UserEntity user, final ResourceEntity resource, final EntityManager em) {
        final Optional<ReservationData> reservationData = resource.getReservationData();

        if (!reservationData.isPresent())
            throw new NotReserved(String.format("Resource id %d is not reserved", resource.getId()));

        final String occupierName = reservationData
                .map(ReservationData::getOccupier)
                .map(UserEntity::getName)
                .get();

        if (!Objects.equals(user.getName(), occupierName))
            throw new ReservedByDifferentUser(String.format(
                    "Resource %d was reserved by user %s",
                    resource.getId(),
                    user.getName()
            ));

        resource.free();
        em.merge(resource);

    }
}
