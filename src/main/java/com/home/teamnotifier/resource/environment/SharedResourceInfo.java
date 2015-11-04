package com.home.teamnotifier.resource.environment;

import com.fasterxml.jackson.annotation.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE,
    creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("SharedResource")
public class SharedResourceInfo {
  private final String name;

  @JsonCreator
  public SharedResourceInfo(@JsonProperty("name") final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
