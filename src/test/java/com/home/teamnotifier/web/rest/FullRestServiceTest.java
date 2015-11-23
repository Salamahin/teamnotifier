package com.home.teamnotifier.web.rest;

import com.home.teamnotifier.NotifierApplication;
import com.home.teamnotifier.authentication.AuthenticationInfo;
import com.home.teamnotifier.core.responses.action.ActionInfo;
import com.home.teamnotifier.core.responses.action.ActionsInfo;
import com.home.teamnotifier.core.responses.status.EnvironmentsInfo;
import com.home.teamnotifier.core.responses.status.OccupationInfo;
import com.home.teamnotifier.core.responses.status.SharedResourceInfo;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;
import io.dropwizard.auth.basic.BasicCredentials;
import io.federecio.dropwizard.junitrunner.DropwizardJunitRunner;
import io.federecio.dropwizard.junitrunner.DropwizardTestConfig;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.home.teamnotifier.DbPreparer.getRandomString;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.port;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(DropwizardJunitRunner.class)
@DropwizardTestConfig(applicationClass = NotifierApplication.class, yamlFile = "/web.yml")
public class FullRestServiceTest {
    private String token;

    private IntegrationTestHelper helper;

    private BasicCredentials credentials;

    @Test
    public void testReserve()
            throws Exception {
        final int resourceId = helper.getAnyPersistedSharedResourceId();
        final String username = credentials.getUsername();

        Optional<OccupationInfo> occupationInfo = getOccupationInfo(resourceId);
        assertThat(occupationInfo).isEmpty();

        reserve(resourceId);

        occupationInfo = getOccupationInfo(resourceId);
        assertThat(occupationInfo).isPresent();
        assertThat(occupationInfo.get().getUserName()).isEqualTo(username);
    }

    private Optional<OccupationInfo> getOccupationInfo(final int resourceId) {
        final EnvironmentsInfo status = getStatus();

        return status.getEnvironments().stream()
                .flatMap(e -> e.getServers().stream())
                .flatMap(s -> s.getResources().stream())
                .filter(r -> r.getId() == resourceId)
                .findFirst()
                .map(SharedResourceInfo::getOccupationInfo);
    }

    private void reserve(final int resourceId) {
        given()
                .auth().oauth2(token)
                .expect().statusCode(HttpStatus.NO_CONTENT_204)
                .when().post("/teamnotifier/1.0/environment/application/reserve/" + resourceId);
    }

    private EnvironmentsInfo getStatus() {
        final Response response = given().auth().oauth2(token)
                .when().get("/teamnotifier/1.0/environment")
                .then().contentType(ContentType.JSON).extract().response();

        return helper.deserialize(EnvironmentsInfo.class, response.asString());
    }

    @Test
    public void testFree()
            throws Exception {
        final int resourceId = helper.getAnyPersistedSharedResourceId();
        final String username = credentials.getUsername();

        reserve(resourceId);

        Optional<OccupationInfo> occupationInfo = getOccupationInfo(resourceId);
        assertThat(occupationInfo).isPresent();
        assertThat(occupationInfo.get().getUserName()).isEqualTo(username);

        free(resourceId);

        occupationInfo = getOccupationInfo(resourceId);
        assertThat(occupationInfo).isEmpty();
    }

    private void free(final int resourceId) {
        given()
                .auth().oauth2(token)
                .expect().statusCode(HttpStatus.NO_CONTENT_204)
                .when().delete("/teamnotifier/1.0/environment/application/reserve/" + resourceId);
    }

    @Test
    public void testSubscribe()
            throws Exception {
        final int serverId = helper.getAnyPersistedServerId();
        final String userName = credentials.getUsername();

        Set<String> subscriberNames = getSubscribersNames(serverId);
        assertThat(subscriberNames).doesNotContain(userName);

        subscribe(serverId);

        subscriberNames = getSubscribersNames(serverId);
        assertThat(subscriberNames).contains(userName);
    }

    private Set<String> getSubscribersNames(final int serverId) {
        final EnvironmentsInfo status = getStatus();

        return status.getEnvironments().stream()
                .flatMap(e -> e.getServers().stream())
                .filter(s -> s.getId() == serverId)
                .flatMap(s -> s.getSubscribers().stream())
                .collect(toSet());
    }

    private void subscribe(final int serverId) {
        given()
                .auth().oauth2(token)
                .expect().statusCode(HttpStatus.NO_CONTENT_204)
                .when().post("/teamnotifier/1.0/environment/server/subscribe/" + serverId);
    }

    @Test
    public void testUnsubscribe()
            throws Exception {
        final int serverId = helper.getAnyPersistedServerId();
        final String userName = credentials.getUsername();

        subscribe(serverId);
        Set<String> subscriberNames = getSubscribersNames(serverId);
        assertThat(subscriberNames).contains(userName);

        unsubscribe(serverId);
        subscriberNames = getSubscribersNames(serverId);
        assertThat(subscriberNames).doesNotContain(userName);
    }

    private void unsubscribe(final int serverId) {
        given()
                .auth().oauth2(token)
                .expect().statusCode(HttpStatus.NO_CONTENT_204)
                .when().delete("/teamnotifier/1.0/environment/server/subscribe/" + serverId);
    }

    @Test
    public void testAuthenticatedUserCanGetServerInfo()
            throws Exception {
        given()
                .auth().oauth2(token)
                .expect().statusCode(HttpStatus.OK_200).contentType(ContentType.JSON)
                .when().get("/teamnotifier/1.0/environment");
    }

    @Test
    public void testNotAuthenticatedUserCanNotGetServerInfo()
            throws Exception {
        given()
                .expect().statusCode(HttpStatus.UNAUTHORIZED_401)
                .when().get("/teamnotifier/1.0/environment");
    }

    @Test
    public void testGetActionsInfo()
            throws Exception {
        final String action = getRandomString();
        final int resourceId = helper.getAnyPersistedSharedResourceId();

        final Instant from = Instant.now();
        createNewInfo(action, resourceId);
        final Instant to = Instant.now();

        final Header headerFrom = new Header("ActionsFrom", encodeInstantToBase64(from));
        final Header headerTo = new Header("ActionsTo", encodeInstantToBase64(to));

        final Response response = given().auth().oauth2(token).header(headerFrom).header(headerTo)
                .when().get("/teamnotifier/1.0/environment/application/action/" + resourceId)
                .then().contentType(ContentType.JSON).extract().response();

        final ActionsInfo info = helper.deserialize(ActionsInfo.class, response.asString());
        assertThat(getDescriptions(info)).contains(action);
    }

    private void createNewInfo(final String action, final int resourceId) {
        given()
                .auth().oauth2(token).header(new Header("ActionDetails", action))
                .expect().statusCode(HttpStatus.NO_CONTENT_204)
                .when().post("/teamnotifier/1.0/environment/application/action/" + resourceId);
    }

    private String encodeInstantToBase64(final Instant time) {
        return new String(Base64.getEncoder().encode(time.toString().getBytes()));
    }

    private List<String> getDescriptions(final ActionsInfo info) {
        return info.getActions().stream()
                .map(ActionInfo::getDescription)
                .collect(toList());
    }

    @Test
    public void testRegistration()
            throws Exception {
        given().auth().preemptive().basic(getRandomString(), getRandomString()).
                expect().statusCode(HttpStatus.NO_CONTENT_204)
                .when().post("/teamnotifier/1.0/users/register");
    }

    @Test
    public void testAuthentication()
            throws Exception {
        given().auth().preemptive().basic(credentials.getUsername(), credentials.getPassword()).
                expect().statusCode(HttpStatus.OK_200).contentType(ContentType.JSON)
                .when().get("/teamnotifier/1.0/users/authenticate");
    }

    @Test
    public void testIncorrectLogin()
            throws Exception {
        given().auth().preemptive().basic(credentials.getUsername(), getRandomString())
                .expect().statusCode(HttpStatus.NO_CONTENT_204)
                .when().get("/teamnotifier/1.0/users/authenticate");
    }

    @Before
    public void setUp() {
        port = 7998;

        helper = new IntegrationTestHelper();
        helper.prepareEnvironment();

        credentials = helper.createNewPersistedUser();
        token = getToken(credentials.getUsername(), credentials.getPassword());
    }

    private String getToken(final String userName, final String password) {
        final Response response =
                given().auth().preemptive().basic(userName, password)
                        .when().get("/teamnotifier/1.0/users/authenticate")
                        .then().contentType(ContentType.JSON).extract().response();

        return helper.deserialize(AuthenticationInfo.class, response.asString()).getToken();
    }
}