package com.home.teamnotifier.storage;

import com.home.teamnotifier.dataobjects.DatabaseObject;


public interface DAO<T extends DatabaseObject> {
  T createOrUpdate(T entity);
  T read(int id);
  void delete(T entity);
}
