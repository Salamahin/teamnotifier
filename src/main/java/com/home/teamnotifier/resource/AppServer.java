package com.home.teamnotifier.resource;

import com.fasterxml.jackson.annotation.*;
import com.google.common.collect.ImmutableSet;
import java.util.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE,
    creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("AppServer")
public class AppServer {
  private final String name;
  private final Set<SharedApplication> resources;

  @JsonCreator
  public AppServer(
      @JsonProperty("name") final String name,
      @JsonProperty("resources") final Collection<SharedApplication> resources
  ) {
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
