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
public class SharedResource {
  private final String name;

  @JsonCreator
  public SharedResource(@JsonProperty("name") final String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
