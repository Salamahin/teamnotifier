package com.home.teamnotifier.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.teamnotifier.*;
import com.home.teamnotifier.authentication.AuthenticationInfo;
import com.home.teamnotifier.core.responses.EnvironmentInfo;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.jackson.Jackson;
import io.federecio.dropwizard.junitrunner.*;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.*;
import org.junit.runner.RunWith;
import java.io.IOException;
import static com.home.teamnotifier.TestHelper.getRandomString;
import static com.jayway.restassured.RestAssured.*;

@RunWith(DropwizardJunitRunner.class)
@DropwizardTestConfig(applicationClass = NotifierApplication.class, yamlFile = "/web.yml")
public class EnvironmentRestServiceTest {
  private String TOKEN;

  private static IntegrationTestHelper HELPER;

  @BeforeClass
  public static void prepare() {
    HELPER = new IntegrationTestHelper();
    HELPER.prepareEnvironment();
  }

  @Before
  public void setUp() {
    port = 7998;
    final BasicCredentials credentials = HELPER.getPersistedUserCredentials();
    TOKEN = getToken(credentials.getUsername(), credentials.getPassword());
  }

  private static String getToken(final String userName, final String password) {
    authentication = preemptive().basic(userName, password);
    final Response response = when()
        .get("/teamnotifier/1.0/users/authenticate")
        .then()
        .contentType(ContentType.JSON)
        .extract()
        .response();

    return HELPER.getAuthInfoDeserialized(response.asString()).getToken();
  }


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
  public void testAuthenticatedUserCanGetServerInfo()
  throws Exception {
    given()
        .auth().oauth2(TOKEN)
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

  }

  @Test
  public void testNewInfo()
  throws Exception {
    final String action = getRandomString();
    final int resourceId = HELPER.getAnyPersistedSharedResourceId();


  }
}