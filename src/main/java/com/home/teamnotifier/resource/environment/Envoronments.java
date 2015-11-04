package com.home.teamnotifier.resource.environment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Envoronments {
  private final List<Environment> environments;

  @JsonCreator
  public Envoronments(@JsonProperty("environments") final List<Environment> environments) {
    this.environments = environments;
  }
}
