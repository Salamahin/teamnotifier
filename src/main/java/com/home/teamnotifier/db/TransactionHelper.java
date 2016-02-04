package com.home.teamnotifier.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.function.Function;

public final class TransactionHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionHelper.class);

    private EntityManagerFactory factory;

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
        } catch (Exception exc) {
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
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
        } catch (RuntimeException e) {
            LOGGER.error("Failed to rollback", e);
        }
    }

    public void start() {
        if (factory == null || !factory.isOpen())
            factory = Persistence.createEntityManagerFactory("teamnotifier");
    }

    public void stop() {
        if (factory == null || !factory.isOpen())
            return;

        factory.close();
    }
}
