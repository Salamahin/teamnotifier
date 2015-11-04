package com.home.teamnotifier.routine;

import com.home.teamnotifier.resource.Environment;
import java.util.*;

public class ResourceMonitor {
  private final List<Integer> reservedApplications;

  public ResourceMonitor() {
    reservedApplications = new ArrayList<>();
  }

  public void reserve(final String securityToken, final int applicationId) {
    fireNotification();
    if(reservedApplications.contains(applicationId))
      throw new AlreadyReserved();

    reservedApplications.add(applicationId);
  }

  public void subscribe(final String securityToken, final int serverId) {

  }

  public void unsubscribe(final String securityToken, final int serverId) {

  }

  void fireNotification() {

  }

  public List<Environment> getStatus(final String securityToken) {
    return null;
  }
}
