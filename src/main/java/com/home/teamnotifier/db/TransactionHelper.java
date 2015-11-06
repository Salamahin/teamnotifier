package com.home.teamnotifier.db;

import com.google.common.base.Throwables;
import org.slf4j.*;
import javax.inject.Inject;
import javax.persistence.*;
import java.util.concurrent.Semaphore;
import java.util.function.Function;

public final class TransactionHelper {
  private static final Logger LOG = LoggerFactory.getLogger(TransactionHelper.class);

  private static final Semaphore MUTEX = new Semaphore(1, true);

  private final EntityManager entityManager;

  @Inject
  public TransactionHelper() {
    EntityManagerFactory managerFactory = Persistence
        .createEntityManagerFactory("teamnotifier");
    entityManager = managerFactory.createEntityManager();
  }

  public <U> U transaction(Function<EntityManager, U> function) {
    U result = null;

    final EntityTransaction transaction = entityManager.getTransaction();
    try {
      MUTEX.acquire();
      transaction.begin();
      result = function.apply(entityManager);
      entityManager.flush();
      transaction.commit();
    } catch (Exception exc) {
      entityManager.flush();
      transaction.rollback();
      Throwables.propagate(exc);
    } finally {
      MUTEX.release();
    }

    return result;
  }
}
