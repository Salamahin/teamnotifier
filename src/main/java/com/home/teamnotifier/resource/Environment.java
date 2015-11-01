package com.home.teamnotifier.resource;

import com.google.common.collect.*;
import java.util.*;

public class Environment {
  private final String name;

  private final Set<AppServer> servers;

  public Environment(final String name, final Collection<AppServer> servers) {
    this.name = name;
    this.servers = ImmutableSet.copyOf(servers);
  }

  public String getName() {
    return name;
  }

  public Set<AppServer> getServers() {
    return servers;
  }
}
