package com.home.teamnotifier.resource.environment;

import com.fasterxml.jackson.annotation.*;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE,
    creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("Environments")
public class EnvironmentsInfo {
  private final List<EnvironmentInfo> environments;

  @JsonCreator
  public EnvironmentsInfo(@JsonProperty("environments") final List<EnvironmentInfo> environments) {
    this.environments = environments;
  }
}
