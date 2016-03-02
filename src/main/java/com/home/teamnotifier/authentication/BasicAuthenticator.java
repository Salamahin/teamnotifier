package com.home.teamnotifier.authentication;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.home.teamnotifier.gateways.NoSuchUser;
import com.home.teamnotifier.gateways.UserCredentials;
import com.home.teamnotifier.gateways.UserGateway;
import com.home.teamnotifier.utils.PasswordHasher;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import java.util.Objects;

public class BasicAuthenticator implements Authenticator<BasicCredentials, UserPrincipal> {
    private final UserGateway userGateway;

    @Inject
    public BasicAuthenticator(final UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    private boolean providedCredentialsAreCorrect(final BasicCredentials provided, final UserCredentials persisted) {
        final String providedPassHash = PasswordHasher.toMd5Hash(provided.getPassword());
        return Objects.equals(providedPassHash, persisted.getPassHash());
    }

    @Override
    public Optional<UserPrincipal> authenticate(final BasicCredentials basicCredentials) throws AuthenticationException {
        try {
            return getUser(basicCredentials);
        } catch (NoSuchUser e) {
            return Optional.absent();
        }
    }

    private Optional<UserPrincipal> getUser(BasicCredentials basicCredentials) {
        final UserCredentials credentials = userGateway.userCredentials(basicCredentials.getUsername());

        if (providedCredentialsAreCorrect(basicCredentials, credentials))
            return Optional.of(UserPrincipal.basic(credentials.getId(), credentials.getUserName()));

        return Optional.absent();
    }
}
