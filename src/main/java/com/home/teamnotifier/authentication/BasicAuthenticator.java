package com.home.teamnotifier.authentication;

import com.google.common.base.Optional;
import com.home.teamnotifier.gateways.NoSuchUser;
import com.home.teamnotifier.gateways.UserCredentials;
import com.home.teamnotifier.gateways.UserGateway;
import com.home.teamnotifier.utils.PasswordHasher;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import java.util.Objects;

public class BasicAuthenticator implements Authenticator<BasicCredentials, BasicPrincipal> {
    private final UserGateway userGateway;

    public BasicAuthenticator(final UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    private boolean compareCredentials(final BasicCredentials provided, final UserCredentials persisted) {
        if (persisted == null) {
            return false;
        }

        final String providedPassHash = PasswordHasher.toMd5Hash(provided.getPassword());
        return Objects.equals(providedPassHash, persisted.getPassHash());
    }

    @Override
    public Optional<BasicPrincipal> authenticate(final BasicCredentials basicCredentials) throws AuthenticationException {
        try {
            final UserCredentials credentials = userGateway.userCredentials(basicCredentials.getUsername());

            if(compareCredentials(basicCredentials, credentials))
                return Optional.of(new BasicPrincipal(credentials.getUserName(), credentials.getId()));

            return Optional.absent();
        } catch (NoSuchUser e) {
            return Optional.absent();
        }
    }
}
