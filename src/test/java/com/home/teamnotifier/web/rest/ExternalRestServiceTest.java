package com.home.teamnotifier.web.rest;

import com.home.teamnotifier.NotifierApplication;
import com.home.teamnotifier.NotifierConfiguration;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class ExternalRestServiceTest {

    @ClassRule
    public static final DropwizardAppRule<NotifierConfiguration> RULE = new DropwizardAppRule<>(
            NotifierApplication.class, ResourceHelpers.resourceFilePath("web.yml")
    );

    @Test
    public void testGetApplicationToken() throws Exception {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("test client");

//        Response response = client.target(
//                String.format("http://localhost:%d/login", RULE.getLocalPort()))
//                .request()
//                .post(Entity.json(loginForm()));
//
//        assertThat(response.getStatus()).isEqualTo(302);
    }
}