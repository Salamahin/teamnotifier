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

class IntegrationTestHelper {
    private final DbPreparer preparer;

    private final ObjectMapper mapper;

    private EnvironmentsInfo environment;

    public IntegrationTestHelper() {
        preparer = new DbPreparer();
        mapper = Jackson.newObjectMapper();
    }

    public void prepareEnvironment() {
        environment = createEnvironment();
    }

    private AppServerAvailabilityChecker getMockedServerChecker() {
        AppServerAvailabilityChecker mocked = mock(AppServerAvailabilityChecker.class);
        when(mocked.getAvailability()).thenReturn(ImmutableMap.of());
        return mocked;
    }

    private EnvironmentsInfo createEnvironment() {
        preparer.createPersistedEnvironmentWithOneServerAndOneResource(
                getRandomString(),
                getRandomString(),
                getRandomString()
        );
        final DbEnvironmentGateway gateway = new DbEnvironmentGateway(preparer.TRANSACTION_HELPER, getMockedServerChecker());
        return gateway.status();
    }

    public BasicCredentials createNewPersistedUser() {
        final String userName = getRandomString();
        final String pass = getRandomString();
        preparer.createPersistedUser(userName, pass);
        return new BasicCredentials(userName, pass);
    }

    public <T> T deserialize(final Class<T> tClass, final String json) {
        try {
            return mapper.readValue(json, tClass);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public int getAnyPersistedServerId() {
        final Optional<Integer> persistedServerId = getPersistedAppServer()
                .map(AppServerInfo::getId);

        if (!persistedServerId.isPresent()) {
            throw new IllegalStateException("No persisted servers");
        }

        return persistedServerId.get();
    }

    private Optional<AppServerInfo> getPersistedAppServer() {
        return Optional.of(environment)
                .map(EnvironmentsInfo::getEnvironments)
                .flatMap(this::getFirstInCollection)
                .map(EnvironmentInfo::getServers)
                .flatMap(this::getFirstInCollection);
    }

    public int getAnyPersistedSharedResourceId() {
        final Optional<Integer> persistedSharedResourceId = getPersistedAppServer()
                .map(AppServerInfo::getResources)
                .flatMap(this::getFirstInCollection)
                .map(SharedResourceInfo::getId);

        if (!persistedSharedResourceId.isPresent()) {
            throw new IllegalStateException("No persisted shared resource");
        }

        return persistedSharedResourceId.get();
    }

    private <T> Optional<T> getFirstInCollection(Collection<T> elems) {
        final Iterator<T> iterator = elems.iterator();
        if (iterator.hasNext()) {
            return Optional.of(iterator.next());
        }
        return Optional.empty();
    }
}
