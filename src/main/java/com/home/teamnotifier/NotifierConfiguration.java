package com.home.teamnotifier;

import com.bazaarvoice.dropwizard.assets.AssetsBundleConfiguration;
import com.bazaarvoice.dropwizard.assets.AssetsConfiguration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

class NotifierConfiguration extends Configuration implements AssetsBundleConfiguration{

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
    ExecutorsConfiguration getExecutorsConfiguration() {
        return executorsConfiguration;
    }

    @JsonProperty(value = "authentication")
    AuthenticationConfiguration getAuthenticationConfiguration() {
        return authenticationConfiguration;
    }

    @Override
    public AssetsConfiguration getAssetsConfiguration() {
        return assets;
    }

    @SuppressWarnings("unused")
    static class AuthenticationConfiguration {
        @Valid
        @NotNull
        @SuppressWarnings("unused")
        private String jwtSecret;

        @JsonProperty(value = "jwtSecret")
        String getJwtSecret() {
            return jwtSecret;
        }
    }

    @SuppressWarnings("unused")
    static class ExecutorsConfiguration {
        @Valid
        @NotNull
        @SuppressWarnings("unused")
        private Integer poolSize;

        @JsonProperty(value = "poolSize")
        Integer getPoolSize() {
            return poolSize;
        }
    }
}
