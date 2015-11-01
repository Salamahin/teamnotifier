package com.home.teamnotifier.storage;

import com.home.teamnotifier.dataobjects.DatabaseObject;
import javax.persistence.*;
import java.util.*;

abstract class AbstractDAO<T extends DatabaseObject> implements DAO<T> {

  protected final EntityManager manager;

  protected Map<String, String> getProperties() {
    return new HashMap<>();
  }

  protected AbstractDAO() {
    Map<String, String> properties = getProperties();
    EntityManagerFactory managerFactory = Persistence.createEntityManagerFactory
        ("jpa_query_builder", properties);
    manager = managerFactory.createEntityManager();
  }

  @Override
  public T createOrUpdate(final T entity) {
    return manager.merge(entity);
  }

  @Override
  public T read(final int id) {
    return null;
  }

  @Override
  public void delete(final int id) {

  }
}
