package com.home.teamnotifier.resource.environment;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Set;

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
      @JsonProperty("resources") final Collection<SharedApplication> resources) {
    this.name = name;
    this.resources = ImmutableSet.copyOf(resources);
  }

  public static class SharedApplication {
    private final String name;

    public SharedApplication(final String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }
  }
}
