package com.home.teamnotifier.db;

import com.home.teamnotifier.core.responses.status.OccupationInfo;
import com.home.teamnotifier.core.responses.status.ResourceInfo;
import com.home.teamnotifier.core.responses.status.ServerInfo;
import com.home.teamnotifier.gateways.exceptions.NoSuchServer;
import com.home.teamnotifier.gateways.exceptions.NoSuchUser;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

final class DbGatewayCommons {
    private DbGatewayCommons() {
        throw new AssertionError();
    }

    static UserEntity getUserEntity(final String userName, final EntityManager em) {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<UserEntity> cq = cb.createQuery(UserEntity.class);
        final Root<UserEntity> rootEntry = cq.from(UserEntity.class);
        final CriteriaQuery<UserEntity> selectUserQuery = cq
                .where(cb.equal(rootEntry.get("name"), userName));

        try {
            return em.createQuery(selectUserQuery).getSingleResult();
        } catch (NoResultException exc) {
            throw new NoSuchUser(String.format("No user with name %s found", userName), exc);
        }
    }

    static ServerEntity getServerEntity(final int serverId, final EntityManager em) {
        final ServerEntity serverEntity = em.find(ServerEntity.class, serverId);
        if (serverEntity == null)
            throw new NoSuchServer(String.format("No server with id %d", serverId));
        return serverEntity;
    }

    static List<String> getSubscribersButUser(final String userName, final ServerEntity server) {
        return server.getImmutableSetOfSubscribers().stream()
                .filter(s -> !Objects.equals(s, userName))
                .collect(toList());
    }

    static ServerInfo toServerInfo(
            final ServerEntity entity,
            final Map<ServerEntity, Boolean> availabilityMap
    ) {
        final List<ResourceInfo> resources = entity.getImmutableSetOfResources().stream()
                .map(DbGatewayCommons::toResource)
                .collect(toList());

        return new ServerInfo(
                entity,
                resources,
                availabilityMap.get(entity)
        );
    }

    private static ResourceInfo toResource(final ResourceEntity resourceEntity) {
        final OccupationInfo occupationInfo = resourceEntity.getReservationData()
                .map(od ->
                        new OccupationInfo(
                                od.getOccupier().getName(),
                                od.getOccupationTime()
                        )
                )
                .orElse(null);

        return new ResourceInfo(
                resourceEntity.getId(),
                resourceEntity.getName(),
                occupationInfo
        );
    }
}
