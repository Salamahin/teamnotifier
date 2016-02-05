package com.home.teamnotifier.db;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.home.teamnotifier.gateways.AppServerGateway;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Set;

public class DbAppServerGateway implements AppServerGateway {
    private final TransactionHelper transactionHelper;

    @Inject
    public DbAppServerGateway(final TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }

    @Override
    public Set<AppServerEntity> getImmutableSetOfObservableServers() {
        final List<AppServerEntity> servers = transactionHelper.transaction(em -> {
            final CriteriaBuilder cb = em.getCriteriaBuilder();
            final CriteriaQuery<AppServerEntity> cq = cb.createQuery(AppServerEntity.class);

            final Root<AppServerEntity> rootEntry = cq.from(AppServerEntity.class);
            final Predicate withNotEmptyCheckUrl = cb.isNotNull(rootEntry.get("statusUrl"));

            return em.createQuery(cq.where(withNotEmptyCheckUrl)).getResultList();
        });

        return ImmutableSet.copyOf(servers);
    }
}
