package com.home.teamnotifier.db;

import org.slf4j.*;
import javax.persistence.*;
import java.util.function.Function;

public final class TransactionHelper {
  private final EntityManager entityManager;

  private TransactionHelper() {
    EntityManagerFactory managerFactory = Persistence
        .createEntityManagerFactory("teamnotifier");
    entityManager = managerFactory.createEntityManager();
  }

  public static TransactionHelper getInstance() {
    return InstanceHolder.INSTANCE;
  }

  private static class InstanceHolder {
    private static final TransactionHelper INSTANCE = new TransactionHelper();
  }

  public <U> U transaction(Function<EntityManager, U> function) {
    final U result;

    try {
      entityManager.getTransaction().begin();
      result = function.apply(entityManager);
      entityManager.flush();
      entityManager.getTransaction().commit();
    } catch (Exception exc) {
      entityManager.getTransaction().rollback();
      entityManager.flush();
      throw exc;
    }

    return result;
  }
}
