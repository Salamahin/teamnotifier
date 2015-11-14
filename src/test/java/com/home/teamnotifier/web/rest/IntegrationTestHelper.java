package com.home.teamnotifier.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.teamnotifier.TestHelper;
import com.home.teamnotifier.authentication.AuthenticationInfo;
import com.home.teamnotifier.core.responses.*;
import com.home.teamnotifier.db.DbEnvironmentGateway;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.jackson.Jackson;
import java.io.IOException;
import java.util.*;
import static com.home.teamnotifier.TestHelper.getRandomString;

class IntegrationTestHelper {
  private final TestHelper helper;
  private final ObjectMapper mapper;

  private BasicCredentials persistedUserCredentials;
  private EnvironmentsInfo environment;

  public IntegrationTestHelper() {
    helper = new TestHelper();
    mapper = Jackson.newObjectMapper();;
  }

  public void prepareEnvironment() {
    persistedUserCredentials = createNewPersistedUser();
    environment = createEnvironment();
  }

  private BasicCredentials createNewPersistedUser() {
    final String userName = getRandomString();
    final String pass = getRandomString();
    helper.createPersistedUser(userName, pass);
    return new BasicCredentials(userName, pass);
  }

  private EnvironmentsInfo createEnvironment() {
    helper.createPersistedEnvironmentWithOneServerAndOneResource(
        getRandomString(),
        getRandomString(),
        getRandomString()
    );
    final DbEnvironmentGateway gateway = new DbEnvironmentGateway(helper.TRANSACTION_HELPER);
    return gateway.status();
  }

  public BasicCredentials getPersistedUserCredentials() {
    return persistedUserCredentials;
  }

  public int getAnyPersistedServerId() {
    final Optional<Integer> persistedServerId = getPersistedAppServer()
        .map(AppServerInfo::getId);

    if(!persistedServerId.isPresent())
      throw new IllegalStateException("No persisted servers");

    return persistedServerId.get();
  }

  public int getAnyPersistedSharedResourceId() {
    final Optional<Integer> persistedSharedResourceId = getPersistedAppServer()
        .map(AppServerInfo::getResources)
        .flatMap(this::getFirstInCollection)
        .map(SharedResourceInfo::getId);

    if(!persistedSharedResourceId.isPresent())
      throw new IllegalStateException("No persisted shared resource");

    return persistedSharedResourceId.get();
  }

  private Optional<AppServerInfo> getPersistedAppServer() {
    return Optional.of(environment)
        .map(EnvironmentsInfo::getEnvironments)
        .flatMap(this::getFirstInCollection)
        .map(EnvironmentInfo::getServers)
        .flatMap(this::getFirstInCollection);
  }

  private <T> Optional<T> getFirstInCollection(Collection<T> elems) {
    final Iterator<T> iterator = elems.iterator();
    if (iterator.hasNext()) { return Optional.of(iterator.next()); }
    return Optional.empty();
  }


  public AuthenticationInfo getAuthInfoDeserialized(final String json) {
    try {
      return mapper.readValue(json, AuthenticationInfo.class);
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public EnvironmentInfo getEnvInfoDeserialized(final String json) {
    try {
      return mapper.readValue(json, EnvironmentInfo.class);
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
