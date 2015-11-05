package com.home.teamnotifier.resource.environment;

import com.fasterxml.jackson.annotation.*;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE,
    creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("AppServer")
public class AppServerInfo {

  private final String name;

  private final Set<SharedResourceInfo> resources;

  private final Set<String> subscribers;

  @JsonCreator
  public AppServerInfo(
      @JsonProperty("name") final String name,
      @JsonProperty("resources") final Set<SharedResourceInfo> resources,
      @JsonProperty("subscribers") final Set<String> subscibers
  ) {
    this.name = name;
    this.resources = ImmutableSet.copyOf(resources);
    this.subscribers = ImmutableSet.copyOf(subscibers);
  }
}
