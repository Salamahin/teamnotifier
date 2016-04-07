package com.home.teamnotifier.db;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.home.teamnotifier.gateways.ServerGateway;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Set;

public class DbServerGateway implements ServerGateway {
    private final TransactionHelper transactionHelper;

    @Inject
    public DbServerGateway(final TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }

    @Override
    public Set<ServerEntity> getImmutableSetOfObservableServers() {
        final List<ServerEntity> servers = transactionHelper.transaction(em -> {
            final CriteriaBuilder cb = em.getCriteriaBuilder();
            final CriteriaQuery<ServerEntity> cq = cb.createQuery(ServerEntity.class);

            final Root<ServerEntity> rootEntry = cq.from(ServerEntity.class);
            final Predicate withNotEmptyCheckUrl = cb.isNotNull(rootEntry.get("statusUrl"));

            return em.createQuery(cq.where(withNotEmptyCheckUrl)).getResultList();
        });

        return ImmutableSet.copyOf(servers);
    }
}
