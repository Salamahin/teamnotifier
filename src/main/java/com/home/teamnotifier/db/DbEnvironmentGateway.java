package com.home.teamnotifier.db;

import com.google.inject.Inject;
import com.home.teamnotifier.core.responses.status.*;
import com.home.teamnotifier.gateways.EnvironmentGateway;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

public class DbEnvironmentGateway implements EnvironmentGateway {
    private final TransactionHelper transactionHelper;

    @Inject
    public DbEnvironmentGateway(final TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }

    @Override
    public EnvironmentsInfo status() {
        return new EnvironmentsInfo(
                loadListFromDb().stream()
                        .map(this::toEnvironment)
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

    private EnvironmentInfo toEnvironment(final EnvironmentEntity entity) {
        return new EnvironmentInfo(
                entity.getName(),
                entity.getImmutableListOfAppServers().stream()
                        .map(this::toAppSever)
                        .collect(toList())
        );
    }

    private AppServerInfo toAppSever(final AppServerEntity entity) {
        final List<SharedResourceInfo> resources = entity.getImmutableListOfResources().stream()
                .map(this::toResource)
                .collect(toList());

        return new AppServerInfo(
                entity.getId(),
                entity.getName(),
                resources,
                entity.getImmutableListOfSubscribers()
        );
    }

    private SharedResourceInfo toResource(final SharedResourceEntity sharedResourceEntity) {
        final OccupationInfo occupationInfo = sharedResourceEntity.getReservationData()
                .map(od -> new OccupationInfo(
                                od.getOccupier().getName(),
                                od.getOccupationTime()
                        )
                )
                .orElse(null);

        return new SharedResourceInfo(
                sharedResourceEntity.getId(),
                sharedResourceEntity.getName(),
                occupationInfo
        );
    }
}
