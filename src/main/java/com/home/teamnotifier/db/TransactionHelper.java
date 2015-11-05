package com.home.teamnotifier.db;

import javax.inject.Inject;
import javax.persistence.*;
import java.util.concurrent.Semaphore;
import java.util.function.Function;

public final class TransactionHelper {
  private static final Semaphore MUTEX = new Semaphore(1, true);

  private final EntityManager entityManager;

  @Inject
  public TransactionHelper() {
    EntityManagerFactory managerFactory = Persistence
        .createEntityManagerFactory("teamnotifier");
    entityManager = managerFactory.createEntityManager();
  }

  public <U> U transaction(Function<EntityManager, U> function) {
    final U result;

    try {
      MUTEX.acquire();
      entityManager.getTransaction().begin();
      result = function.apply(entityManager);
      entityManager.flush();
      entityManager.getTransaction().commit();
    } catch (Exception exc) {
      entityManager.flush();
      entityManager.getTransaction().rollback();
      throw new TransactionError(exc);
    } finally {
      MUTEX.release();
    }

    return result;
  }
}
