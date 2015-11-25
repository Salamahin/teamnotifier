package com.home.teamnotifier.db;

import com.google.common.base.Throwables;
import com.google.inject.Inject;
import com.home.teamnotifier.gateways.*;
import com.home.teamnotifier.utils.PasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.validation.ConstraintViolationException;

import java.util.Optional;

import static com.home.teamnotifier.db.DbGatewayCommons.getUserEntity;

public class DbUserGateway implements UserGateway {
    private static final Logger LOGGER = LoggerFactory.getLogger(DbUserGateway.class);

    private final TransactionHelper transactionHelper;

    @Inject
    public DbUserGateway(final TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;
    }

    @Override
    public UserCredentials userCredentials(final int id) {
        try {
            final UserEntity entity = transactionHelper.transaction(em -> em.find(UserEntity.class, id));
            return new UserCredentials(entity.getId(), entity.getName(), entity.getPassHash());
        } catch (EntityNotFoundException exc) {
            throw new NoSuchUser(String.format("No user with id %d", id), exc);
        }
    }

    @Override
    public UserCredentials userCredentials(final String userName) {
        final UserEntity entity = getEntityByName(userName);
        return new UserCredentials(entity.getId(), entity.getName(), entity.getPassHash());
    }

    @Override
    public void newUser(final String userName, final String password) {
        LOGGER.info("New user {} creation", userName);
        final UserEntity entity = new UserEntity(userName, PasswordHasher.toMd5Hash(password));
        try {
            transactionHelper.transaction(em -> em.merge(entity));
        } catch (Exception exc) {
            rethrowConstraintViolation(exc);
        }
    }

    private void rethrowConstraintViolation(Exception exc) {
        final Optional<Throwable> firstConstraintViolation = Throwables.getCausalChain(exc).stream()
                .filter((ConstraintViolationException.class)::isInstance)
                .findFirst();

        if(firstConstraintViolation.isPresent())
            throw new InvalidCredentials(firstConstraintViolation.get());
        else
            Throwables.propagate(exc);
    }

    private UserEntity getEntityByName(String name) {
        try {
            return transactionHelper.transaction(em -> getUserEntity(name, em));
        } catch (NoResultException exc) {
            throw new NoSuchUser(String.format("No user with name %s", name), exc);
        }
    }
}
