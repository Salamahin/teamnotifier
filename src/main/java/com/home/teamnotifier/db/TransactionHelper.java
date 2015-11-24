package com.home.teamnotifier.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.function.Function;

public final class TransactionHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionHelper.class);

    private static final EntityManagerFactory factory = Persistence
            .createEntityManagerFactory("teamnotifier");

    public <U> U transaction(Function<EntityManager, U> function) {
        U result = null;
        EntityManager em = null;
        EntityTransaction tx = null;

        try {
            em = factory.createEntityManager();
            tx = em.getTransaction();
            tx.begin();
            result = function.apply(em);
            tx.commit();
        } catch (PersistenceException exc) {
            rollback(tx);
            throw exc;
        } finally {
            if (em != null) {
                em.close();
            }
        }

        return result;
    }

    private void rollback(EntityTransaction tx) {
        try {
            if (tx != null) {
                tx.rollback();
            }
        } catch (RuntimeException e) {
            LOGGER.error("Failed to rollback", e);
        }
    }
}
