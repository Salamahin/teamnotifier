package com.home.teamnotifier.storage;

import com.home.teamnotifier.dataobjects.DatabaseObject;
import org.slf4j.*;
import javax.persistence.*;
import java.util.*;

abstract class AbstractDAO<T extends DatabaseObject> implements DAO<T> {

  private final Logger LOG = LoggerFactory.getLogger(AbstractDAO.class);

  protected final EntityManager manager;

  private final Class<T> tClass;

  protected Map<String, String> getProperties() {
    return new HashMap<>();
  }

  protected AbstractDAO(final Class<T> tClass) {
    this.tClass = tClass;
    Map<String, String> properties = getProperties();
    EntityManagerFactory managerFactory = Persistence.createEntityManagerFactory
        ("teamnotifier");
    manager = managerFactory.createEntityManager();
  }

  @Override
  public T createOrUpdate(T entity) {
    try {
      manager.getTransaction().begin();
      entity = manager.merge(entity);
      manager.getTransaction().commit();
    } catch (Exception exc) {
      LOG.error("Create or update failed", exc);
      manager.getTransaction().rollback();
    }

    return entity;
  }

  @Override
  public T read(final int id) {
    try {
      return manager.find(tClass, id);
    } catch (Exception exc) {
      LOG.error("Failed to find", exc);
    }
    return null;
  }

  @Override
  public void delete(final T entity) {
    try {
      manager.getTransaction().begin();
      manager.remove(entity);
      manager.getTransaction().commit();
    } catch (Exception exc) {
      LOG.error("Failed to delete", exc);
      manager.getTransaction().rollback();
    }
  }
}
