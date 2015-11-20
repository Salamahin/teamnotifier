package com.home.teamnotifier.core.responses.status;

import com.fasterxml.jackson.annotation.*;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.*;

import static java.util.stream.Collectors.toList;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE,
    creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("EnvironmentInfo")
public class EnvironmentInfo {

  private final String name;

  private final List<AppServerInfo> servers;

  @JsonCreator
  public EnvironmentInfo(
      @JsonProperty("name") final String name,
      @JsonProperty("servers") final List<AppServerInfo> servers
  ) {
    this.name = name;
    this.servers = ImmutableList.copyOf(servers);
  }

  public String getName() {
    return name;
  }

  public List<AppServerInfo> getServers() {
    return servers;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, servers);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) { return true; }
    if (o == null || getClass() != o.getClass()) { return false; }
    final EnvironmentInfo that = (EnvironmentInfo) o;
    return Objects.equals(name, that.name) &&
        Objects.equals(servers, that.servers);
  }
}
