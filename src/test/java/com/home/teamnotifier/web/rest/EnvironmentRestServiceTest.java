package com.home.teamnotifier.web.rest;

import com.home.teamnotifier.NotifierApplication;
import com.home.teamnotifier.TestHelper;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import io.federecio.dropwizard.junitrunner.DropwizardJunitRunner;
import io.federecio.dropwizard.junitrunner.DropwizardTestConfig;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import static com.home.teamnotifier.TestHelper.getRandomString;
import static com.jayway.restassured.RestAssured.*;

@RunWith(DropwizardJunitRunner.class)
@DropwizardTestConfig(applicationClass = NotifierApplication.class, yamlFile = "/web.yml")
public class EnvironmentRestServiceTest {
    @BeforeClass
    public static void setUp() {
        port = 7998;
        createPersistedUser(getRandomString(), getRandomString());
    }

    private static void createPersistedUser(final String userName, final String password) {
        TestHelper helper = new TestHelper();
        helper.createPersistedUser(userName, password);
    }
//
//    private static String getToken(final String userName, final String password) {
//        authentication = basic(userName, password);
//        final Response response = when()
//                .get("/rides")
//                .then()
//                .contentType(ContentType.JSON)
//                .extract()
//                .response();
//
//
//
//    }
}