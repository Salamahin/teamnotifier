package com.home.teamnotifier.routine;

import com.home.teamnotifier.resource.environment.Environment;
import java.util.*;

public class ResourceMonitor {
  private final Set<Integer> reservedApplications;

  public ResourceMonitor() {
    reservedApplications = new HashSet<>();
  }

  public void reserve(final int userId, final int applicationId) {
    if (reservedApplications.contains(applicationId)) {
      throw new AlreadyReserved();
    }

    reservedApplications.add(applicationId);
    fireNotification();
  }

  public void subscribe(final int userId, final int serverId) {

  }

  public void unsubscribe(final int userId, final int serverId) {

  }

  void fireNotification() {

  }

  public void free(final int userId, final int applicationId) {
    if (!reservedApplications.contains(applicationId)) {
      throw new NotReserved();
    }

    reservedApplications.remove(applicationId);
    fireNotification();
  }

  public List<Environment> getStatus(final int userId) {
    return null;
  }
}
