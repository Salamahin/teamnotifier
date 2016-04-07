package com.home.teamnotifier.gateways;

public class ResourceDescription {
    private final String environmentName;
    private final String serverName;
    private final String resourceName;

    private ResourceDescription(final Builder builder) {
        environmentName = builder.environmentName;
        serverName = builder.serverName;
        resourceName = builder.resourceName;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public String getServerName() {
        return serverName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public static final class Builder {
        private String environmentName;
        private String serverName;
        private String resourceName;

        private Builder() {
        }

        public Builder withEnvironmentName(String val) {
            environmentName = val;
            return this;
        }

        public Builder withServerName(String val) {
            serverName = val;
            return this;
        }

        public Builder withResourceName(String val) {
            resourceName = val;
            return this;
        }

        public ResourceDescription build() {
            return new ResourceDescription(this);
        }
    }
}
