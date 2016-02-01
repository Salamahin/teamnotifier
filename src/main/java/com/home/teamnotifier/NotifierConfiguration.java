package com.home.teamnotifier;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class NotifierConfiguration extends Configuration {

    @Valid
    @NotNull
    private ExecutorsConfiguration executorsConfiguration;

    @Valid
    @NotNull
    private AuthenticationConfiguration authenticationConfiguration;

    @JsonProperty(value = "executors")
    public ExecutorsConfiguration getExecutorsConfiguration() {
        return executorsConfiguration;
    }

    @JsonProperty(value = "authentication")
    public AuthenticationConfiguration getAuthenticationConfiguration() {
        return authenticationConfiguration;
    }

    public static class AuthenticationConfiguration {
        @Valid
        @NotNull
        private String jwtSecret;

        @JsonProperty(value = "jwtSecret")
        public String getJwtSecret() {
            return jwtSecret;
        }
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
