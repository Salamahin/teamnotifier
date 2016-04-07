package com.home.teamnotifier;

import com.bazaarvoice.dropwizard.assets.AssetsBundleConfiguration;
import com.bazaarvoice.dropwizard.assets.AssetsConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class NotifierConfiguration extends Configuration implements AssetsBundleConfiguration{

    @Valid
    @NotNull
    @SuppressWarnings("unused")
    private ExecutorsConfiguration executorsConfiguration;

    @Valid
    @NotNull
    @SuppressWarnings("unused")
    private AuthenticationConfiguration authenticationConfiguration;

    @JsonProperty
    @Valid
    @NotNull
    private final AssetsConfiguration assets = new AssetsConfiguration();

    @JsonProperty(value = "executors")
    public ExecutorsConfiguration getExecutorsConfiguration() {
        return executorsConfiguration;
    }

    @JsonProperty(value = "authentication")
    public AuthenticationConfiguration getAuthenticationConfiguration() {
        return authenticationConfiguration;
    }

    @Override
    public AssetsConfiguration getAssetsConfiguration() {
        return assets;
    }

    @SuppressWarnings("unused")
    public static class AuthenticationConfiguration {
        @Valid
        @NotNull
        @SuppressWarnings("unused")
        private String jwtSecret;

        @JsonProperty(value = "jwtSecret")
        public String getJwtSecret() {
            return jwtSecret;
        }
    }

    @SuppressWarnings("unused")
    public static class ExecutorsConfiguration {
        @Valid
        @NotNull
        @SuppressWarnings("unused")
        private Integer poolSize;

        @JsonProperty(value = "poolSize")
        public Integer getPoolSize() {
            return poolSize;
        }
    }
}
