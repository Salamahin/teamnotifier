package com.home.teamnotifier;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public class NotifierConfiguration extends Configuration {
  private final static String GENERATED_SECRET_STRING;
  static {
    final String s1 = UUID.randomUUID().toString();
    final String s2 = UUID.randomUUID().toString();

    final StringBuilder sb = new StringBuilder();
    for (int i = 0; i < s1.length(); i++) {
      sb.append((char) (s1.charAt(i) ^ s2.charAt(i % s2.length())));
    }
    GENERATED_SECRET_STRING = sb.toString();
  }

  @NotNull
  private final String tokenSecretString = GENERATED_SECRET_STRING;

  @Valid
  @NotNull
  private ExecutorsConfiguration executorsConfiguration;

  @JsonProperty(value = "executors")
  public ExecutorsConfiguration getExecutorsConfiguration() {
    return executorsConfiguration;
  }


  public byte[] getJwtTokenSecret() {
    return tokenSecretString.getBytes();
  }
  public static class ExecutorsConfiguration {

    @Valid
    @NotNull
    private Integer poolSize;
    @JsonProperty(value = "poolSize")
    public Integer getPoolSize() {
      return poolSize;
    }

  }
}
