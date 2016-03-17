package com.home.teamnotifier.core.responses;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.dropwizard.jackson.Jackson;

import java.io.IOException;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.StrictAssertions.assertThat;

public final class SerializationTestHelper {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private SerializationTestHelper() {
        throw new AssertionError();
    }

    public static <T> void testSerializesToJson(final Class<T> tClass, final T fineInstance, final String fixture) throws IOException {
        final String expected = MAPPER.writeValueAsString(MAPPER.readValue(fixture(fixture), tClass));
        assertThat(MAPPER.writeValueAsString(fineInstance)).isEqualTo(expected);
    }

    public static <T> void testDeserializeFromJson(final Class<T> tClass, final T fineInstance, final String fineFixture) throws IOException {
        final T expected = MAPPER.readValue(fixture(fineFixture), tClass);
        assertThat(expected).isEqualTo(fineInstance);
    }

}
