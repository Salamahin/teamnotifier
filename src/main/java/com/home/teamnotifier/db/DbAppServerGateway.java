package com.home.teamnotifier.db;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.home.teamnotifier.gateways.AppServerGateway;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class DbAppServerGateway implements AppServerGateway {
    private final TransactionHelper transactionHelper;

    @Inject
    public DbAppServerGateway(final TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }

    @Override
    public ImmutableList<AppServerEntity> getObservableServers() {
        final List<AppServerEntity> servers = transactionHelper.transaction(em -> {
            final CriteriaBuilder cb = em.getCriteriaBuilder();
            final CriteriaQuery<AppServerEntity> cq = cb.createQuery(AppServerEntity.class);
            final Root<AppServerEntity> rootEntry = cq.from(AppServerEntity.class);
            final Predicate withNotEmptyCheckUrl = cb.isNotNull(rootEntry.get("statusUrl"));

            final CriteriaQuery<AppServerEntity> serversToCheck = cq.select(rootEntry).where(withNotEmptyCheckUrl);

            final TypedQuery<AppServerEntity> query = em.createQuery(serversToCheck);

            return query.getResultList();
        });

        return ImmutableList.copyOf(servers);
    }
}
