package com.home.teamnotifier.core.environment;

import com.fasterxml.jackson.annotation.*;
import com.google.common.collect.ImmutableList;
import java.util.List;

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
}
