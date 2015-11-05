package com.home.teamnotifier.gateways;

import com.home.teamnotifier.db.*;
import org.junit.*;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

public class DbUserGatewayTest {

  private TransactionHelper helper;

  private DbUserGateway userGateway;

  private UserEntity user;

  @Before
  public void setUp()
  throws Exception {
    helper = new TransactionHelper();
    userGateway = new DbUserGateway(helper);
    user = createPersistedUser();
  }

  private UserEntity createPersistedUser() {
    final UserEntity entity = new UserEntity();
    entity.setName(getRandomString());
    entity.setPassHash(getRandomString());
    return helper.transaction(em -> em.merge(entity));
  }

  private String getRandomString() {
    return UUID.randomUUID().toString();
  }

  @Test
  public void testUserCredentials()
  throws Exception {
    final UserCredentials credentials = userGateway.userCredentials(user.getName());
    assertThat(credentials.getUserName()).isEqualTo(user.getName());
    assertThat(credentials.getPassHash()).isEqualTo(user.getPassHash());
  }
}