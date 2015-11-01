package com.home.teamnotifier.resource;

import com.google.common.collect.ImmutableSet;
import java.util.*;

public class AppServer {
  private final String name;
  private final Set<SharedApplication> resources;

  public AppServer(final String name, final Collection<SharedApplication> resources) {
    this.name = name;
    this.resources = ImmutableSet.copyOf(resources);
  }

  public String getName() {
    return name;
  }

  public Set<SharedApplication> getResources() {
    return resources;
  }
}
