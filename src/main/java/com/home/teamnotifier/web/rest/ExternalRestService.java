package com.home.teamnotifier.web.rest;

import com.home.teamnotifier.authentication.AuthenticationInfo;
import com.home.teamnotifier.authentication.application.AppTokenCreator;
import com.home.teamnotifier.authentication.application.AppTokenPrincipal;
import com.home.teamnotifier.authentication.session.SessionTokenPrincipal;
import com.home.teamnotifier.core.ResourceMonitor;
import com.home.teamnotifier.gateways.ResourceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import static com.home.teamnotifier.utils.Base64Decoder.decodeBase64;

@RestController
@RequestMapping("1.0/external")
public class ExternalRestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalRestService.class);

    private final ResourceMonitor resourceMonitor;
    private final AppTokenCreator creator;

    @Autowired
    public ExternalRestService(final ResourceMonitor resourceMonitor, final AppTokenCreator creator) {
        this.resourceMonitor = resourceMonitor;
        this.creator = creator;
    }

    @RequestMapping(method = RequestMethod.POST,path = "/action")
    public void newResourceAction(
            @AuthenticationPrincipal final AppTokenPrincipal userPrincipal,
            @RequestParam("environment") final String environmentName,
            @RequestParam("server") final String serverName,
            @RequestParam("application") final String resourceName,
            @RequestParam("details") final String base64EncodedDetails
    ) {
        final String userName = userPrincipal.getName();
        final String decodedDetails = decodeBase64(base64EncodedDetails);

        LOGGER.info("User {} new action on resource {} {} ({}) request", userName, serverName, resourceName, decodedDetails);

        final ResourceDescription resourceDescription = ResourceDescription.newBuilder()
                .withResourceName(resourceName)
                .withServerName(serverName)
                .withEnvironmentName(environmentName)
                .build();

        resourceMonitor.newResourceAction(userName, resourceDescription, decodedDetails);
    }

    @RequestMapping(method = RequestMethod.GET,path = "/token")
    public AuthenticationInfo getApplicationToken(
            final HttpServletRequest request,
            @AuthenticationPrincipal final SessionTokenPrincipal principal
    ) {
        final String remoteAddr = request.getRemoteAddr();
        LOGGER.info("{} requested new application token (endpoint {})", principal.getName(), remoteAddr);
        return new AuthenticationInfo(creator.getTokenFor(principal.getId(), remoteAddr));
    }
}
