package com.home.teamnotifier.core.environment;

import com.fasterxml.jackson.annotation.*;
import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE,
    creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("SharedResource")
public class SharedResourceInfo {

  private final int id;

  private final String name;

  private final OccupationInfo occupationInfo;

  @JsonCreator
  public SharedResourceInfo(
      @JsonProperty("id")  final int id, @JsonProperty("name") final String name,
      @JsonProperty("occupationInfo") final OccupationInfo occupationInfo
  ) {
    this.id = id;
    this.name = name;
    this.occupationInfo = occupationInfo;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) { return true; }
    if (o == null || getClass() != o.getClass()) { return false; }
    final SharedResourceInfo that = (SharedResourceInfo) o;
    return Objects.equals(name, that.name) &&
        Objects.equals(id, that.id) &&
        Objects.equals(occupationInfo, that.occupationInfo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, occupationInfo, id);
  }
}
