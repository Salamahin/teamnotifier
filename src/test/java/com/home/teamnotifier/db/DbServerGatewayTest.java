package com.home.teamnotifier.db;

import com.home.teamnotifier.DbPreparer;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.home.teamnotifier.DbPreparer.getRandomString;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class DbServerGatewayTest {
    private static final DbPreparer helper = new DbPreparer();

    private DbServerGateway gateway;

    private EnvironmentEntity getNotPersistedEnvironmentEntity() {
        return new EnvironmentEntity(getRandomString());
    }

    private void store(final EnvironmentEntity notPersistedEntity) {
        helper.TRANSACTION_HELPER.transaction(em -> {
            em.merge(notPersistedEntity);
            return null;
        });
    }


    @Before
    public void setUp() throws Exception {
        gateway = new DbServerGateway(helper.TRANSACTION_HELPER);
    }

    @Test
    public void testGetServersWithNotNullUrls() throws Exception {

        final EnvironmentEntity env = getNotPersistedEnvironmentEntity();
        env.newAppServer("srv1");
        env.newAppServer("srv2", "http://localhost");
        env.newAppServer("srv3", "http://localhost:8080");

        store(env);

        final List<String> servers = gateway.getImmutableSetOfObservableServers().stream()
                .map(AppServerEntity::getName)
                .collect(toList());

        assertThat(servers).contains("srv2", "srv3").doesNotContain("srv1");
    }
}
