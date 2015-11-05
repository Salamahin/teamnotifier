package com.home.teamnotifier.routine;

import com.home.teamnotifier.gateways.AlreadyReserved;
import com.home.teamnotifier.gateways.NotReserved;
import com.home.teamnotifier.resource.environment.EnvironmentInfo;
import java.util.*;

public class ResourceMonitor {
  private final Set<Integer> reservedApplications;

  public ResourceMonitor() {
    reservedApplications = new HashSet<>();
  }

  public void reserve(final String userName, final int applicationId) {
    if (reservedApplications.contains(applicationId)) {
      throw new AlreadyReserved();
    }

    reservedApplications.add(applicationId);
    fireNotification();
  }

  public void subscribe(final String userName, final int serverId) {

  }

  public void unsubscribe(final String userName, final int serverId) {

  }

  void fireNotification() {

  }

  public void free(final String userName, final int applicationId) {
    if (!reservedApplications.contains(applicationId)) {
      throw new NotReserved();
    }

    reservedApplications.remove(applicationId);
    fireNotification();
  }

  public List<EnvironmentInfo> getStatus(final String userName) {
    return null;
  }
}
