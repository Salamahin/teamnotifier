package com.home.teamnotifier.core.responses;

import com.fasterxml.jackson.annotation.*;
import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE,
    creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("OccupationInfo")
public class OccupationInfo {
  private final String userName;

  private final String occupationTime;

  @JsonCreator
  public OccupationInfo(
      @JsonProperty("userName") final String userName,
      @JsonProperty("occupationTime") final String occupationTime
  ) {
    this.userName = userName;
    this.occupationTime = occupationTime;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) { return true; }
    if (o == null || getClass() != o.getClass()) { return false; }
    final OccupationInfo that = (OccupationInfo) o;
    return Objects.equals(userName, that.userName) &&
        Objects.equals(occupationTime, that.occupationTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userName, occupationTime);
  }
}
