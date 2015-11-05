package com.home.teamnotifier.resource.environment;

import com.fasterxml.jackson.annotation.*;
import java.time.LocalDateTime;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE,
    creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("SharedResource")
public class OccupationInfo {
  private final String userName;

  private final LocalDateTime occupationTime;

  @JsonCreator
  public OccupationInfo(
      @JsonProperty("userName") final String userName,
      @JsonProperty("occupationTime") final LocalDateTime occupationTime
  ) {
    this.userName = userName;
    this.occupationTime = occupationTime;
  }
}
