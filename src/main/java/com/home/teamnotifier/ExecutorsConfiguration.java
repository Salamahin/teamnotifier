package com.home.teamnotifier;

import javax.validation.constraints.NotNull;

public class ExecutorsConfiguration {
  @NotNull
  private Integer poolSize;

  public Integer getPoolSize() {
    return poolSize;
  }
}
