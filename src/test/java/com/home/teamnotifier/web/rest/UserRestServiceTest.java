package com.home.teamnotifier.web.rest;

import com.home.teamnotifier.NotifierApplication;
import com.jayway.restassured.http.ContentType;
import io.dropwizard.auth.basic.BasicCredentials;
import io.federecio.dropwizard.junitrunner.*;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.*;
import org.junit.runner.RunWith;
import static com.home.teamnotifier.DbPreparer.getRandomString;
import static com.jayway.restassured.RestAssured.*;

@RunWith(DropwizardJunitRunner.class)
@DropwizardTestConfig(applicationClass = NotifierApplication.class, yamlFile = "/web.yml")
public class UserRestServiceTest {

  private BasicCredentials credentials;

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
    final IntegrationTestHelper helper = new IntegrationTestHelper();
    helper.prepareEnvironment();
    credentials = helper.createNewPersistedUser();
  }
}