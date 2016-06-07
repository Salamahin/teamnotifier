package com.home.teamnotifier.db;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.home.teamnotifier.core.ServerAvailabilityChecker;
import com.home.teamnotifier.core.responses.status.ServerInfo;
import com.home.teamnotifier.gateways.ServerGateway;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Set;

import static com.home.teamnotifier.db.DbGatewayCommons.getServerEntity;
import static com.home.teamnotifier.db.DbGatewayCommons.toServerInfo;

public class DbServerGateway implements ServerGateway {
    private final TransactionHelper transactionHelper;
//    private final ServerAvailabilityChecker serverAvailabilityChecker;

    @Inject
    DbServerGateway(final TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
//        this.serverAvailabilityChecker = serverAvailabilityChecker;
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

    @Override
    public ServerInfo getInfoForServer(int serverId) {
        return null; //FIXME
//        return transactionHelper.transaction(em -> {
//            final ServerEntity s = getServerEntity(serverId, em);
//            return toServerInfo(s, serverAvailabilityChecker.getAvailability());
//        });
    }
}
