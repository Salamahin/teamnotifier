package com.home.teamnotifier.db;

import com.home.teamnotifier.DbPreparer;
import com.home.teamnotifier.gateways.InvalidCredentials;
import com.home.teamnotifier.gateways.NoSuchUser;
import org.junit.Before;
import org.junit.Test;

import static com.home.teamnotifier.DbPreparer.getRandomString;
import static org.assertj.core.api.Assertions.assertThat;

public class DbUserGatewayTest {
    private static final DbPreparer helper = new DbPreparer();

    private DbUserGateway userGateway;

    private UserEntity user;

    @Test
    public void testCanCreateNewUser() throws Exception {
        final String userName = getRandomString();
        final String password = getRandomString();
        userGateway.newUser(userName, password);

        final UserEntity credentials = userGateway.get(userName);
        assertThat(credentials.getName()).isEqualTo(userName);
    }

    @Test
    public void testUserCredentials() throws Exception {
        final UserEntity credentials = userGateway.get(user.getName());
        assertThat(credentials.getName()).isEqualTo(user.getName());
        assertThat(credentials.getPassHash()).isEqualTo(user.getPassHash());
    }

    @Test(expected = NoSuchUser.class)
    public void testIncorrectLoginThrowsNoSuchUser() throws Exception {
        userGateway.get(getRandomString());
    }

    @Test(expected = NoSuchUser.class)
    public void testIncorrectUserIdThrowsNoSuchUser() throws Exception {
        userGateway.get(-1);
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