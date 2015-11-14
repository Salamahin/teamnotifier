package com.home.teamnotifier.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.teamnotifier.*;
import com.home.teamnotifier.authentication.AuthenticationInfo;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import io.dropwizard.jackson.Jackson;
import io.federecio.dropwizard.junitrunner.*;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.*;
import java.io.IOException;
import static com.home.teamnotifier.TestHelper.getRandomString;
import static com.jayway.restassured.RestAssured.*;

@RunWith(DropwizardJunitRunner.class)
@DropwizardTestConfig(applicationClass = NotifierApplication.class, yamlFile = "/web.yml")
public class EnvironmentRestServiceTest {
  private static final Logger LOG = LoggerFactory.getLogger(EnvironmentRestService.class);

  private static String TOKEN;

  @Test
  public void testReserve()
  throws Exception {

  }

  @Test
  public void testFree()
  throws Exception {

  }

  @Test
  public void testSubscribe()
  throws Exception {

  }

  @Test
  public void testUnsubscribe()
  throws Exception {

  }

  @Test
  public void testGetServerInfo()
  throws Exception {
    given()
        .log().all()
        .auth().oauth2(TOKEN)
        .expect()
        .statusCode(HttpStatus.OK_200)
        .contentType(ContentType.JSON)
        .when()
        .get("/teamnotifier/1.0/environment");
  }

  @Test
  public void testGetActionsInfo()
  throws Exception {

  }

  @Test
  public void testNewInfo()
  throws Exception {

  }

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
}