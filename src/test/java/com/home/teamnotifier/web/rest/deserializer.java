package com.home.teamnotifier.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.home.teamnotifier.DbPreparer;
import com.home.teamnotifier.core.AppServerAvailabilityChecker;
import com.home.teamnotifier.core.responses.status.AppServerInfo;
import com.home.teamnotifier.core.responses.status.EnvironmentInfo;
import com.home.teamnotifier.core.responses.status.EnvironmentsInfo;
import com.home.teamnotifier.core.responses.status.SharedResourceInfo;
import com.home.teamnotifier.db.DbEnvironmentGateway;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.jackson.Jackson;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import static com.home.teamnotifier.DbPreparer.getRandomString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class Deserializer {

    private final ObjectMapper mapper;

    public Deserializer() {
        mapper = Jackson.newObjectMapper();
    }

    public <T> T deserialize(final Class<T> tClass, final String json) {
        try {
            return mapper.readValue(json, tClass);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
