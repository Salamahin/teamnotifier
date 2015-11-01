package com.home.teamnotifier.domains;

import org.slf4j.*;
import javax.persistence.*;
import java.util.function.Function;

public final class PersistentHelper {
  private static final Logger LOG = LoggerFactory.getLogger(PersistentHelper.class);

  private final EntityManager entityManager;

  private PersistentHelper() {
    EntityManagerFactory managerFactory = Persistence
        .createEntityManagerFactory("teamnotifier");
    entityManager = managerFactory.createEntityManager();
  }

  public static PersistentHelper getInstance() {
    return InstanceHolder.INSTANCE;
  }

  private static class InstanceHolder {
    private static final PersistentHelper INSTANCE = new PersistentHelper();
  }

  public <T, U> U transaction(Function<EntityManager, U> function) {
    U result = null;
    try {
      entityManager.getTransaction().begin();
      result = function.apply(entityManager);
      entityManager.getTransaction().commit();
    } catch (Exception exc) {
      LOG.error("Failed to execute", exc);
      handleError();
    }
    return result;
  }

  private void handleError() {
    entityManager.getTransaction().rollback();
    throw new TransactionFailure();
  }

  public static class TransactionFailure extends RuntimeException {

  }
}
