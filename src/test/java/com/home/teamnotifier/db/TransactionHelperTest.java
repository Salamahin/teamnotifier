package com.home.teamnotifier.db;

import org.junit.*;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

public class TransactionHelperTest {

  private TransactionHelper transactionHelper;

  @Test
  public void testStoredObjectHasNotNullId()
  throws Exception {
    final SharedResource resource = newResourceWithRandomName();
    final SharedResource persistetResource = store(resource);
    assertThat(persistetResource.getId()).isNotNull();
  }

  private SharedResource newResourceWithRandomName() {
    final SharedResource resource = new SharedResource();
    resource.setName(getRandomName());
    return resource;
  }

  private <T> T store(final T entity) {
    return transactionHelper.transaction(em -> em.merge(entity));
  }

  private String getRandomName() {return UUID.randomUUID().toString();}

  @Test
  public void testDbStoresObjectAfterUpdate()
  throws Exception {
    AppServer persistedServer = newPersistedServerWithRandomName();

    final String oldName = persistedServer.getName();
    final Integer oldId = persistedServer.getId();

    final String newName = getRandomName();
    assertThat(oldName).isNotEqualTo(newName);

    persistedServer.setName(newName);
    persistedServer = update(persistedServer);

    assertThat(persistedServer.getId()).isEqualTo(oldId);
    assertThat(persistedServer.getName()).isEqualTo(newName);
  }

  private AppServer newPersistedServerWithRandomName() {
    final AppServer server = new AppServer();
    server.setName(getRandomName());
    return store(server);
  }

  private <T> T update(final T entity) {
    return store(entity);
  }

  @Test
  public void testFindPersistedEntity()
  throws Exception {
    final int persistedServerId = newPersistedServerWithRandomName().getId();
    final AppServer persistedServer = find(AppServer.class, persistedServerId);
    assertThat(persistedServer.getName()).isNotNull();
  }

  private <T> T find(final Class<T> tClass, int id) {
    return transactionHelper.transaction(em -> em.find(tClass, id));
  }

  @Test
  public void testRemovePersistedEntity()
  throws Exception {
    final AppServer persistedServer = newPersistedServerWithRandomName();
    remove(persistedServer);
    final AppServer findedServer = find(AppServer.class, persistedServer.getId());
    assertThat(findedServer).isNull();
  }

  private <T> void remove(final T persistedEntity) {
    transactionHelper.transaction(em -> {
      em.remove(persistedEntity);
      return null;
    });
  }

  @Before
  public void setUp()
  throws Exception {
    transactionHelper = TransactionHelper.getInstance();
  }
}