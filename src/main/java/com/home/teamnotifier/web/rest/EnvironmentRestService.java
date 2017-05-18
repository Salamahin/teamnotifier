package com.home.teamnotifier.web.rest;

import com.google.common.collect.Range;
import com.home.teamnotifier.authentication.session.SessionTokenPrincipal;
import com.home.teamnotifier.core.ResourceMonitor;
import com.home.teamnotifier.core.responses.action.ResourceActionsHistory;
import com.home.teamnotifier.core.responses.action.ServerActionsHistory;
import com.home.teamnotifier.core.responses.status.EnvironmentsInfo;
import com.home.teamnotifier.core.responses.status.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZonedDateTime;

@RestController
@RequestMapping("1.0/environment")
public class EnvironmentRestService {
    private final Logger LOGGER = LoggerFactory.getLogger(EnvironmentRestService.class);

    private final ResourceMonitor resourceMonitor;

    @Autowired
    public EnvironmentRestService(final ResourceMonitor resourceMonitor) {
        this.resourceMonitor = resourceMonitor;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/application/{applicationId}/reserve")
    public void reserve(
            @AuthenticationPrincipal final SessionTokenPrincipal userPrincipal,
            @PathVariable("applicationId") final Integer applicationId
    ) {
        final String userName = userPrincipal.getName();
        LOGGER.info("User {} reserve resource id {} request", userName, applicationId);
        resourceMonitor.reserve(userName, applicationId);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/application/{applicationId}/reserve")
    public void free(
            @AuthenticationPrincipal final SessionTokenPrincipal userPrincipal,
            @PathVariable("applicationId") final Integer applicationId
    ) {
        final String userName = userPrincipal.getName();
        LOGGER.info("User {} free resource id {} request", userName, applicationId);
        resourceMonitor.free(userName, applicationId);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/server/{serverId}/subscribe")
    public ServerInfo subscribe(
            @AuthenticationPrincipal final SessionTokenPrincipal userPrincipal,
            @PathVariable("serverId") final Integer serverId
    ) {
        final String userName = userPrincipal.getName();
        LOGGER.info("User {} subscribe on server id {} request", userName, serverId);
        return resourceMonitor.subscribe(userName, serverId);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/server/{serverId}/subscribe")
    public void unsubscribe(
            @AuthenticationPrincipal final SessionTokenPrincipal userPrincipal,
            @PathVariable("serverId") final Integer serverId) {
        final String userName = userPrincipal.getName();
        LOGGER.info("User {} unsubscribe from server id {} request", userName, serverId);
        resourceMonitor.unsubscribe(userName, serverId);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/application/{applicationId}/action")
    public void newResourceAction(
            @AuthenticationPrincipal final SessionTokenPrincipal userPrincipal,
            @PathVariable("applicationId") final Integer applicationId,
            @RequestParam("details") final String details
    ) {
        final String userName = userPrincipal.getName();
        LOGGER.info("User {} new action on resource id {} ({}) request", userName, applicationId, details);
        resourceMonitor.newResourceAction(userName, applicationId, details);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/server/{serverId}/action")
    public void newServerAction(
            @AuthenticationPrincipal final SessionTokenPrincipal userPrincipal,
            @PathVariable("serverId") final Integer serverId,
            @RequestParam("details") final String details
    ) {
        final String userName = userPrincipal.getName();
        LOGGER.info("User {} new action on server id {} ({}) request", userName, serverId, details);
        resourceMonitor.newServerAction(userName, serverId, details);
    }

    @RequestMapping(method = RequestMethod.GET)
    public EnvironmentsInfo getServerInfo(@AuthenticationPrincipal final SessionTokenPrincipal userPrincipal) {
        LOGGER.info("User {} status request", userPrincipal.getName());
        return resourceMonitor.status();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/application/{applicationId}/action")
    public ResourceActionsHistory getResourceActionsHistory(
            @AuthenticationPrincipal final SessionTokenPrincipal userPrincipal,
            @PathVariable("applicationId") final Integer applicationId,
            @RequestParam("from") final String fromISO8601,
            @RequestParam("to") final String toISO8601
    ) {
        final Instant fromInstant = ZonedDateTime.parse(fromISO8601).toInstant();
        final Instant toInstant = ZonedDateTime.parse(toISO8601).toInstant();

        LOGGER.info("User {} actions on resource {} from {} to {} request",
                userPrincipal.getName(),
                applicationId,
                fromInstant,
                toInstant
        );

        return resourceMonitor.resourceActions(applicationId, Range.closed(fromInstant, toInstant));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/server/{serverId}/action")
    public ServerActionsHistory getServerActionsHistory(
            @AuthenticationPrincipal final SessionTokenPrincipal userPrincipal,
            @PathVariable("serverId") final Integer serverId,
            @RequestParam("from") final String fromISO8601,
            @RequestParam("to") final String toISO8601
    ) {
        final Instant fromInstant = ZonedDateTime.parse(fromISO8601).toInstant();
        final Instant toInstant = ZonedDateTime.parse(toISO8601).toInstant();

        LOGGER.info("User {} actions on resource {} from {} to {} request",
                userPrincipal.getName(),
                serverId,
                fromInstant,
                toInstant
        );

        return resourceMonitor.serverActions(serverId, Range.closed(fromInstant, toInstant));
    }
}
