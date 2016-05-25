package com.home.teamnotifier.db;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.home.teamnotifier.core.BroadcastInformation;
import com.home.teamnotifier.core.responses.notification.Reservation;
import com.home.teamnotifier.core.responses.notification.Subscription;
import com.home.teamnotifier.core.responses.action.ServerSubscribersInfo;
import com.home.teamnotifier.gateways.SubscriptionGateway;
import com.home.teamnotifier.gateways.exceptions.*;
import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.home.teamnotifier.db.DbGatewayCommons.getSubscribersButUser;
import static com.home.teamnotifier.db.DbGatewayCommons.getUserEntity;

public class DbSubscriptionGateway implements SubscriptionGateway {
    private final TransactionHelper transactionHelper;

    @Inject
    DbSubscriptionGateway(final TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }

    @Override
    public SubscriptionResult subscribe(final String userName, final int serverId) {
        try {
            return transactionHelper.transaction(em -> {
                final UserEntity u = getUserEntity(userName, em);

                ServerEntity s = getServerEntity(serverId, em);

                s.subscribe(u);
                s = em.merge(s);

                final Subscription notification = Subscription.subscribe(u, s);
                final List<String> subscribersButUser = getSubscribersButUser(u.getName(), s);

                final BroadcastInformation<Subscription> messageToOthers = new BroadcastInformation<>(
                        notification,
                        subscribersButUser
                );

                final ServerSubscribersInfo messageToActor = new ServerSubscribersInfo(s);

                return new SubscriptionResult(messageToOthers, messageToActor);
            });

        } catch (Exception exc) {
            rethrowConstraintViolation(exc, userName, serverId);
            return null;
        }
    }

    @Override
    public ServerSubscribersInfo getSubscribers(final int serverId) {
        return transactionHelper.transaction(em -> new ServerSubscribersInfo(getServerEntity(serverId, em)));
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
            final UserEntity u = getUserEntity(userName, em);

            ServerEntity s = getServerEntity(serverId, em);

            if(!s.unsubscribe(u))
                throw new NotSubscribed(String.format("User %s was not subscribed on server %d", userName, serverId));
            s = em.merge(s);

            return new BroadcastInformation<>(
                    Subscription.unsubscribe(u, s),
                    getSubscribersButUser(userName, s)
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
