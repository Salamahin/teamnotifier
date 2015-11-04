package com.home.teamnotifier.resource.environment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE,
    creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("Environment")
public class Environment {
  private final String name;

  private final Set<AppServer> servers;

  @JsonCreator
  public Environment(@JsonProperty("name") final String name,
      @JsonProperty("servers") final Set<AppServer> servers) {
    this.name = name;
    this.servers = servers;
  }

  public String getName() {
    return name;
  }

  public Set<AppServer> getServers() {
    return servers;
  }
}
