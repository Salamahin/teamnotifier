package com.home.teamnotifier.resource.environment;

import com.fasterxml.jackson.annotation.*;
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

  @JsonCreator
  public AppServerInfo(
      @JsonProperty("name") final String name,
      @JsonProperty("resources") final Set<SharedResourceInfo> resources) {
    this.name = name;
    this.resources = resources;
  }
}
