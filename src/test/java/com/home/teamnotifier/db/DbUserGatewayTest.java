package com.home.teamnotifier.db;

import com.home.teamnotifier.gateways.UserCredentials;
import com.home.teamnotifier.utils.PasswordHasher;
import org.junit.*;
import static com.home.teamnotifier.Commons.*;
import static org.assertj.core.api.Assertions.assertThat;

public class DbUserGatewayTest {

  private DbUserGateway userGateway;

  private UserEntity user;

  @Before
  public void setUp()
  throws Exception {
    userGateway = new DbUserGateway(HELPER);
    user = createPersistedUser(getRandomString(), getRandomString());
  }

  @Test
  public void testCanCreateNewUser()
  throws Exception {
    final String userName = getRandomString();
    final String password = getRandomString();
    userGateway.newUser(userName, password);
    final UserCredentials credentials = userGateway.userCredentials(userName);
    assertThat(credentials.getUserName()).isEqualTo(userName);
    assertThat(credentials.getPassHash()).isEqualTo(PasswordHasher.toMd5Hash(password));
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