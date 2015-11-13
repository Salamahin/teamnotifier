package com.home.teamnotifier.web.rest;

import com.home.teamnotifier.NotifierApplication;
import com.home.teamnotifier.TestHelper;
import io.federecio.dropwizard.junitrunner.DropwizardJunitRunner;
import io.federecio.dropwizard.junitrunner.DropwizardTestConfig;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.home.teamnotifier.TestHelper.getRandomString;
import static com.jayway.restassured.RestAssured.*;

@RunWith(DropwizardJunitRunner.class)
@DropwizardTestConfig(applicationClass = NotifierApplication.class, yamlFile = "/web.yml")
public class UserRestServiceTest {

    @BeforeClass
    public static void setUp() {
        port=7998;
    }

    @Test
    public void testRegistration() throws Exception {
        authentication = preemptive().basic(getRandomString(), getRandomString());
        expect()
            .statusCode(HttpStatus.NO_CONTENT_204)
            .when()
            .post("/teamnotifier/1.0/users/register");
    }

    @Test
    public void testAuthentication() throws Exception {
        final String userName = getRandomString();
        final String password = getRandomString();

        createPersistedUser(userName, password);

        authentication = preemptive().basic(userName, password);
        expect()
                .statusCode(HttpStatus.OK_200)
                .when()
                .get("/teamnotifier/1.0/users/authenticate");
    }

    @Test
    public void testIncorrectLogin() throws Exception {
        final String userName = getRandomString();
        final String password = getRandomString();

        createPersistedUser(userName, password);

        authentication = preemptive().basic(userName, getRandomString());
        expect()
                .statusCode(HttpStatus.NO_CONTENT_204)
                .when()
                .get("/teamnotifier/1.0/users/authenticate");
    }

    private void createPersistedUser(final String userName, final String password) {
        TestHelper helper = new TestHelper();
        helper.createPersistedUser(userName, password);
    }
}