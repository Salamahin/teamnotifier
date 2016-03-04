package com.home.teamnotifier;

import com.home.teamnotifier.db.*;
import com.home.teamnotifier.utils.PasswordHasher;

import java.util.Objects;
import java.util.UUID;

import static com.home.teamnotifier.utils.PasswordHasher.*;

public final class DbPreparer {

    public final TransactionHelper TRANSACTION_HELPER;

    public DbPreparer() {
        TRANSACTION_HELPER = new TransactionHelper();
    }

    public static String getRandomString() {
        return UUID.randomUUID().toString();
    }

    public UserEntity createPersistedUser(
            final String userName,
            final String pass
    ) {
        final String salt = UUID.randomUUID().toString();

        final UserEntity entity = new UserEntity(userName, toHash(pass, salt), salt);
        return TRANSACTION_HELPER.transaction(em -> em.merge(entity));
    }

    public EnvironmentEntity createPersistedEnvironmentWithOneServerAndOneResource(
            final String envName,
            final String serverName,
            final String appName
    ) {
        final EnvironmentEntity entity = new EnvironmentEntity(envName);
        final AppServerEntity appServerEntity = entity.newAppServer(serverName);
        appServerEntity.newSharedResource(appName);

        return TRANSACTION_HELPER.transaction(em -> em.merge(entity));
    }

    public int anyServerId(final EnvironmentEntity notEmptyEntity) {
        return notEmptyEntity.getImmutableSetOfAppServers().stream()
                .map(AppServerEntity::getId)
                .findFirst()
                .get();
    }

    public int anyResourceId(final EnvironmentEntity notEmptyEntity, final int serverId) {
        return notEmptyEntity.getImmutableSetOfAppServers().stream()
                .filter(s -> Objects.equals(s.getId(), serverId))
                .flatMap(e -> e.getImmutableSetOfResources().stream())
                .map(SharedResourceEntity::getId)
                .findFirst()
                .get();
    }

    public int anyResourceId(final EnvironmentEntity notEmptyEntity) {
        return notEmptyEntity.getImmutableSetOfAppServers().stream()
                .flatMap(e -> e.getImmutableSetOfResources().stream())
                .map(SharedResourceEntity::getId)
                .findFirst()
                .get();
    }
}
