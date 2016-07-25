package com.home.teamnotifier.authentication.basic;

import com.google.common.base.Optional;
import com.home.teamnotifier.db.UserEntity;
import com.home.teamnotifier.gateways.UserGateway;
import com.home.teamnotifier.gateways.exceptions.NoSuchUser;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import java.util.Objects;

import static com.home.teamnotifier.utils.PasswordHasher.toHash;

public class BasicAuthenticator implements Authenticator<BasicCredentials, BasicPrincipal> {
    private final UserGateway userGateway;

    public BasicAuthenticator(final UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    private boolean providedCredentialsAreCorrect(final BasicCredentials provided, final UserEntity persisted) {
        final String salt = persisted.getSalt();
        final String providedPassHash = toHash(provided.getPassword(), salt);
        return Objects.equals(providedPassHash, persisted.getPassHash());
    }

    @Override
    public Optional<BasicPrincipal> authenticate(final BasicCredentials basicCredentials) throws AuthenticationException {
        try {
            return getUser(basicCredentials);
        } catch (NoSuchUser e) {
            return Optional.absent();
        }
    }

    private Optional<BasicPrincipal> getUser(BasicCredentials basicCredentials) {
        final UserEntity credentials = userGateway.get(basicCredentials.getUsername());

        if (providedCredentialsAreCorrect(basicCredentials, credentials))
            return Optional.of(new BasicPrincipal(credentials.getName(), credentials.getId()));

        return Optional.absent();
    }
}
