package com.home.teamnotifier.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.teamnotifier.NotifierApplication;
import com.home.teamnotifier.TestHelper;
import com.home.teamnotifier.authentication.AuthenticationInfo;
import com.jayway.restassured.authentication.OAuthSignature;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import io.dropwizard.jackson.Jackson;
import io.federecio.dropwizard.junitrunner.DropwizardJunitRunner;
import io.federecio.dropwizard.junitrunner.DropwizardTestConfig;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.home.teamnotifier.TestHelper.getRandomString;
import static com.jayway.restassured.RestAssured.*;

@RunWith(DropwizardJunitRunner.class)
@DropwizardTestConfig(applicationClass = NotifierApplication.class, yamlFile = "/web.yml")
public class EnvironmentRestServiceTest {
    private static final Logger LOG = LoggerFactory.getLogger(EnvironmentRestService.class);
    private static String TOKEN;

    @BeforeClass
    public static void setUp() {
        port = 7998;

        final String userName = getRandomString();
        final String password = getRandomString();
        createPersistedUser(userName, password);

        TOKEN = getToken(userName, password);
    }

    private static void createPersistedUser(final String userName, final String password) {
        TestHelper helper = new TestHelper();
        helper.createPersistedUser(userName, password);
    }

    private static String getToken(final String userName, final String password) {
        authentication = preemptive().basic(userName, password);
        final Response response = when()
                .get("/teamnotifier/1.0/users/authenticate")
                .then()
                .contentType(ContentType.JSON)
                .extract()
                .response();

        return getAuthInfoDeserialized(response.asString()).getToken();
    }

    private static AuthenticationInfo getAuthInfoDeserialized(final String json) {
        ObjectMapper mapper = Jackson.newObjectMapper();
        try {
            return mapper.readValue(json, AuthenticationInfo.class);
        } catch (IOException e) {
            LOG.error("Deserialization fails", e);
            return new AuthenticationInfo("");
        }
    }

    @Test
    public void testReserve() throws Exception {

    }

    @Test
    public void testFree() throws Exception {

    }

    @Test
    public void testSubscribe() throws Exception {

    }

    @Test
    public void testUnsubscribe() throws Exception {

    }

    @Test
    public void testGetServerInfo() throws Exception {
        given().auth().preemptive().oauth2(TOKEN)
                .expect()
                .statusCode(HttpStatus.OK_200)
                .contentType(ContentType.JSON)
                .when()
                .get("/teamnotifier/1.0/environment");
    }

    @Test
    public void testGetActionsInfo() throws Exception {

    }

    @Test
    public void testNewInfo() throws Exception {

    }
}