package com.home.teamnotifier.db;

import com.home.teamnotifier.DbPreparer;
import com.home.teamnotifier.gateways.InvalidCredentials;
import com.home.teamnotifier.gateways.NoSuchUser;
import com.home.teamnotifier.gateways.UserCredentials;
import com.home.teamnotifier.utils.PasswordHasher;
import org.junit.Before;
import org.junit.Test;

import static com.home.teamnotifier.DbPreparer.getRandomString;
import static org.assertj.core.api.Assertions.assertThat;

public class DbUserGatewayTest {
    private static final DbPreparer helper = new DbPreparer();

    private DbUserGateway userGateway;

    private UserEntity user;

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
    public void testUserCredentials() throws Exception {
        final UserCredentials credentials = userGateway.userCredentials(user.getName());
        assertThat(credentials.getUserName()).isEqualTo(user.getName());
        assertThat(credentials.getPassHash()).isEqualTo(user.getPassHash());
    }

    @Test(expected = NoSuchUser.class)
    public void testIncorrectLoginThrowsNoSuchUser() throws Exception {
        userGateway.userCredentials(getRandomString());
    }

    @Test(expected = NoSuchUser.class)
    public void testIncorrectUserIdThrowsNoSuchUser() throws Exception {
        userGateway.userCredentials(-1);
    }

    @Test(expected = InvalidCredentials.class)
    public void testEmptyCredentialsFails() throws Exception {
        final String userName = "";
        final String password = "";
        userGateway.newUser(userName, password);
    }

    @Before
    public void setUp() throws Exception {
        userGateway = new DbUserGateway(helper.TRANSACTION_HELPER);
        user = helper.createPersistedUser(getRandomString(), getRandomString());
    }
}