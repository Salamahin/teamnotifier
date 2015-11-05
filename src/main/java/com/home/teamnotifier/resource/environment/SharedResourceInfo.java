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

  private final OccupationInfo occupationInfo;

  @JsonCreator
  public SharedResourceInfo(
      @JsonProperty("name") final String name,
      @JsonProperty("occupationInfo") final OccupationInfo occupationInfo
  ) {
    this.name = name;
    this.occupationInfo = occupationInfo;
  }
}
