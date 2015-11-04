package com.home.teamnotifier;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class NotifierConfiguration extends Configuration
{
  public static class ExecutorsConfiguration {
    @Valid
    @NotNull
    private Integer poolSize;

    @JsonProperty(value = "poolSize")
    public Integer getPoolSize() {
      return poolSize;
    }
  }

  @Valid
  @NotNull
  private ExecutorsConfiguration executorsConfiguration;

  @JsonProperty(value = "executors")
  public ExecutorsConfiguration getExecutorsConfiguration() {
    return executorsConfiguration;
  }

  @JsonProperty(value = "executors")
  public void setExecutorsConfiguration(final ExecutorsConfiguration executorsConfiguration) {
    this.executorsConfiguration = executorsConfiguration;
  }
}
