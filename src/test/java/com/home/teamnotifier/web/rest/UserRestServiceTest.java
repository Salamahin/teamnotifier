package com.home.teamnotifier.web.rest;

import com.home.teamnotifier.NotifierApplication;
import com.sun.org.apache.xpath.internal.operations.String;
import io.federecio.dropwizard.junitrunner.DropwizardJunitRunner;
import io.federecio.dropwizard.junitrunner.DropwizardTestConfig;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static com.jayway.restassured.RestAssured.*;

@RunWith(DropwizardJunitRunner.class)
@DropwizardTestConfig(applicationClass = NotifierApplication.class, yamlFile = "/web.yml")
public class UserRestServiceTest {

//    @Test
//    public void test() throws Exception {
//        Map<String, String> authenticationParams = new HashMap<>();
//        authenticationParams.put(HttpSt)
//
//        expect()
//                .statusCode(HttpStatus.OK_200)
//                .when()
//                .get("/teamnotifier/1.0/users/register");
//    }

}