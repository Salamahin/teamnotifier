package com.home.teamnotifier.db;

import com.home.teamnotifier.gateways.exceptions.NoSuchUser;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    static List<String> getSubscribersButUser(final String userName, final AppServerEntity server) {
        return server.getImmutableSetOfSubscribers().stream()
                .filter(s -> !Objects.equals(s, userName))
                .collect(Collectors.toList());
    }
}
