package com.home.teamnotifier.db;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.home.teamnotifier.gateways.UserGateway;
import com.home.teamnotifier.gateways.exceptions.InvalidCredentials;
import com.home.teamnotifier.gateways.exceptions.NoSuchUser;
import com.home.teamnotifier.gateways.exceptions.SuchUserAlreadyPresent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.home.teamnotifier.db.DbGatewayCommons.getUserEntity;
import static com.home.teamnotifier.utils.PasswordHasher.toHash;

public class DbUserGateway implements UserGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(DbUserGateway.class);

    private final TransactionHelper transactionHelper;

    @Inject
    public DbUserGateway(final TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }

    @Override
    public UserEntity get(final int id) {
        try {
            return transactionHelper.transaction(em -> {
                final CriteriaBuilder cb = em.getCriteriaBuilder();
                final CriteriaQuery<UserEntity> query = cb.createQuery(UserEntity.class);
                final Root<UserEntity> root = query.from(UserEntity.class);

                final TypedQuery<UserEntity> typedQuery = em.createQuery(
                        query
                                .select(root)
                                .where(cb.equal(root.get("id"), id))
                );

                return typedQuery.getSingleResult();
            });
        } catch (NoResultException exc) {
            throw new NoSuchUser(String.format("No user with id %d", id), exc);
        }
    }

    @Override
    public UserEntity get(final String userName) {
        return getEntityByName(userName);
    }

    @Override
    public void newUser(final String userName, final String password) {
        LOGGER.info("New user {} creation", userName);

        final String salt = UUID.randomUUID().toString();
        final UserEntity entity = new UserEntity(userName, toHash(password, salt), salt);

        try {
            transactionHelper.transaction(em -> {
                em.persist(entity);
                return null;
            });
        } catch (Exception exc) {
            rethrowConstraintViolation(exc);
        }
    }

    @SafeVarargs
    private final Optional<Throwable> unwrapException(
            final Exception e,
            final Class<? extends Throwable> exc,
            final Class<? extends Throwable>... moreExceptions
    ) {
        final List<Class<? extends Throwable>> expectedTypes = Lists.asList(exc, moreExceptions);

        return Throwables.getCausalChain(e).stream()
                .filter(t -> expectedTypes.contains(t.getClass()))
                .findFirst();
    }

    private void rethrowConstraintViolation(Exception exc) {
        final Optional<Throwable> firstConstraintViolation = unwrapException(
                exc,
                org.hibernate.exception.ConstraintViolationException.class,
                javax.validation.ConstraintViolationException.class
        );

        if(firstConstraintViolation.isPresent()) {
            try {
                throw firstConstraintViolation.get();
            } catch (org.hibernate.exception.ConstraintViolationException e) {
                throw new SuchUserAlreadyPresent(e);
            } catch (javax.validation.ConstraintViolationException e) {
                throw new InvalidCredentials(e);
            } catch (Throwable e) {
                throw Throwables.propagate(e);
            }
        }

        throw Throwables.propagate(exc);
    }

    private UserEntity getEntityByName(String name) {
        try {
            return transactionHelper.transaction(em -> getUserEntity(name, em));
        } catch (NoResultException exc) {
            throw new NoSuchUser(String.format("No user with name %s", name), exc);
        }
    }
}
