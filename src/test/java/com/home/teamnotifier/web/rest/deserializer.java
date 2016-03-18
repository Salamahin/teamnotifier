package com.home.teamnotifier.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;

import java.io.IOException;

import static org.mockito.Mockito.mock;

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
