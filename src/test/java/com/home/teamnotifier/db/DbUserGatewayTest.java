package com.home.teamnotifier.db;

import com.home.teamnotifier.gateways.exceptions.InvalidCredentials;
import com.home.teamnotifier.gateways.exceptions.NoSuchUser;
import com.home.teamnotifier.gateways.exceptions.SuchUserAlreadyPresent;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DbUserGatewayTest {
    private DbPreparer preparer;
    private DbUserGateway gateway;

    @Before
    public void setUp() throws Exception {
        preparer = new DbPreparer();
        preparer.initDataBase();
        gateway = new DbUserGateway(preparer.getTransactionHelper());
    }

    @Test(expected = EmptyPassword.class)
    public void testEmptyPasswordThrowsException() {
        final String userName = "user";
        final String password = "";

        gateway.newUser(userName, password);
    }

    @Test
    public void testCanCreateNewUser() throws Exception {
        final String userName = "user";
        final String password = "password";
        gateway.newUser(userName, password);

        final UserEntity credentials = gateway.get(userName);
        assertThat(credentials.getName()).isEqualTo(userName);
    }

    @Test
    public void testUserCredentials() throws Exception {
        final String name = preparer.persistedUserName();

        final UserEntity credentials = gateway.get(name);

        assertThat(credentials.getName()).isEqualTo(name);
        assertThat(credentials.getPassHash()).isEqualTo(preparer.persistedUserPassHash());
        assertThat(credentials.getSalt()).isEqualTo(preparer.persistedUserSalt());
    }

    @Test(expected = NoSuchUser.class)
    public void testIncorrectLoginThrowsNoSuchUser() throws Exception {
        gateway.get("wrong_login");
    }

    @Test(expected = NoSuchUser.class)
    public void testIncorrectUserIdThrowsNoSuchUser() throws Exception {
        gateway.get(-1);
    }

    @Test(expected = InvalidCredentials.class)
    public void testEmptyCredentialsFails() throws Exception {
        final String userName = "";
        final String password = "";
        gateway.newUser(userName, password);
    }

    @Test(expected = SuchUserAlreadyPresent.class)
    public void testSameNameFails() throws Exception {
        gateway.newUser(preparer.persistedUserName(), "pass2");
    }
}