package com.home.teamnotifier.core.responses;

import com.fasterxml.jackson.annotation.*;
import com.google.common.collect.ImmutableList;
import java.util.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE,
    creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonTypeName("Actions")
public class ActionsInfo {
  private final List<ActionInfo> actions;

  @JsonCreator
  public ActionsInfo(@JsonProperty("actions") final List<ActionInfo> actions) {
    this.actions = ImmutableList.copyOf(actions);
  }

  public List<ActionInfo> getActions() {
    return actions;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) { return true; }
    if (o == null || getClass() != o.getClass()) { return false; }
    final ActionsInfo that = (ActionsInfo) o;
    return Objects.equals(actions, that.actions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(actions);
  }
}
