package com.home.teamnotifier.gateways;

import com.home.teamnotifier.db.UserEntity;
import org.junit.*;
import static com.home.teamnotifier.gateways.Commons.*;
import static org.assertj.core.api.Assertions.assertThat;

public class DbUserGatewayTest {

  private DbUserGateway userGateway;

  private UserEntity user;

  @Before
  public void setUp()
  throws Exception {
    userGateway = new DbUserGateway(HELPER);
    user = createPersistedUserWithRandomPassHash(getRandomString());
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
    final UserCredentials credentials = userGateway.userCredentials(getRandomString());
    assertThat(credentials).isNull();
  }
}