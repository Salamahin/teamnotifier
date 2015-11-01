package com.home.teamnotifier.storage;

import com.home.teamnotifier.dataobjects.EnvironmentDataObject;

public class EnvironmentDAO extends AbstractDAO<EnvironmentDataObject> {
  public EnvironmentDAO() {
    super(EnvironmentDataObject.class);
  }
}
