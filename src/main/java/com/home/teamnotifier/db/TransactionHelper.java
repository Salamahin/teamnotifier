package com.home.teamnotifier.db;

import javax.inject.Inject;
import javax.persistence.*;
import java.util.function.Function;

public final class TransactionHelper {
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
      entityManager.getTransaction().begin();
      result = function.apply(entityManager);
      entityManager.flush();
      entityManager.getTransaction().commit();
    } catch (Exception exc) {
      entityManager.flush();
      entityManager.getTransaction().rollback();
      throw exc;
    }

    return result;
  }
}
