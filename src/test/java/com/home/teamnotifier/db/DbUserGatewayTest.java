package com.home.teamnotifier.db;

import com.home.teamnotifier.gateways.UserCredentials;
import org.junit.*;
import static org.assertj.core.api.Assertions.assertThat;

public class DbUserGatewayTest {

  private DbUserGateway userGateway;

  private UserEntity user;

  @Before
  public void setUp()
  throws Exception {
    userGateway = new DbUserGateway(Commons.HELPER);
    user = Commons.createPersistedUserWithRandomPassHash(Commons.getRandomString());
  }

  @Test
  public void testUserCredentials()
  throws Exception {
    final UserCredentials credentials = userGateway.userCredentials(user.getName());
    assertThat(credentials.getUserName()).isEqualTo(user.getName());
    assertThat(credentials.getPassHash()).isEqualTo(user.getPassHash());
  }

  @Test
  public void testIncorrectLoginReturnsNull()
  throws Exception {
    final UserCredentials credentials = userGateway.userCredentials(Commons.getRandomString());
    assertThat(credentials).isNull();
  }
}