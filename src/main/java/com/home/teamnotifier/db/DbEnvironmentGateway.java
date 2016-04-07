package com.home.teamnotifier.db;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.home.teamnotifier.core.ServerAvailabilityChecker;
import com.home.teamnotifier.core.responses.status.*;
import com.home.teamnotifier.gateways.EnvironmentGateway;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class DbEnvironmentGateway implements EnvironmentGateway {
    private final TransactionHelper transactionHelper;
    private final ServerAvailabilityChecker serverAvailabilityChecker;

    @Inject
    public DbEnvironmentGateway(
            final TransactionHelper transactionHelper,
            ServerAvailabilityChecker serverAvailabilityChecker
    ) {
        this.transactionHelper = transactionHelper;
        this.serverAvailabilityChecker = serverAvailabilityChecker;
    }

    @Override
    public EnvironmentsInfo status() {
        final ImmutableMap<ServerEntity, Boolean> availabilityMap = serverAvailabilityChecker.getAvailability();
        return new EnvironmentsInfo(
                loadListFromDb().stream()
                        .map(e -> toEnvironment(e, availabilityMap))
                        .collect(Collectors.toList())
        );
    }

    private List<EnvironmentEntity> loadListFromDb() {
        return transactionHelper.transaction(em -> {
            final CriteriaBuilder cb = em.getCriteriaBuilder();

            final CriteriaQuery<EnvironmentEntity> cq = cb.createQuery(EnvironmentEntity.class);
            final Root<EnvironmentEntity> rootEntry = cq.from(EnvironmentEntity.class);
            final CriteriaQuery<EnvironmentEntity> all = cq.select(rootEntry);
            final TypedQuery<EnvironmentEntity> allQuery = em.createQuery(all);

            return allQuery.getResultList();
        });
    }

    private EnvironmentInfo toEnvironment(final EnvironmentEntity entity, final ImmutableMap<ServerEntity, Boolean> availabilityMap) {
        return new EnvironmentInfo(
                entity.getName(),
                entity.getImmutableSetOfServers().stream()
                        .map(e -> toSever(e, availabilityMap))
                        .collect(toList())
        );
    }

    private ServerInfo toSever(final ServerEntity entity, final ImmutableMap<ServerEntity, Boolean> availabilityMap) {
        final Set<ResourceInfo> resources = entity.getImmutableSetOfResources().stream()
                .map(this::toResource)
                .collect(toSet());

        return new ServerInfo(
                entity.getId(),
                entity.getName(),
                resources,
                entity.getImmutableSetOfSubscribers(),
                availabilityMap.get(entity)
        );
    }

    private ResourceInfo toResource(final ResourceEntity resourceEntity) {
        final OccupationInfo occupationInfo = resourceEntity.getReservationData()
                .map(od -> new OccupationInfo(
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
