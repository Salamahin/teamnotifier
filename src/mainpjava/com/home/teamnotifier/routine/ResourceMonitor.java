package com.home.teamnotifier.routine;

import com.home.teamnotifier.resource.environment.Environment;
import java.util.List;

public class ResourceMonitor {
  public void reserve(final int applicationId) {
    fireNotification();
  }

  void fireNotification() {

  }

  public void subscribe(final int serverId) {

  }

  public void unsubscribe(final int serverId) {

  }

  public List<Environment> status() {
    throw new IllegalStateException("not implemented");
  }
}
