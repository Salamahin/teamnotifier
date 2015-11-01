package com.home.teamnotifier.storage;

import com.home.teamnotifier.dataobjects.SharedResourceDataObject;

public class SharedResourceDAO extends AbstractDAO<SharedResourceDataObject> {
  public SharedResourceDAO() {
    super(SharedResourceDataObject.class);
  }
}
