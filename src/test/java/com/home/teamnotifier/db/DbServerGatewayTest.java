package com.home.teamnotifier.db;

import com.home.teamnotifier.db.tools.MockedCheckerProvider;
import org.dbunit.dataset.builder.DataSetBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

public class DbServerGatewayTest {
    private DbServerGateway gateway;
    private DbPreparer preparer;

    @Before
    public void setUp() throws Exception {
        preparer = new DbPreparer();
        preparer.initDataBase();

        gateway = new DbServerGateway(preparer.getTransactionHelper());
    }

    @Test
    public void testGetServersWithNotNullUrls() throws Exception {
        final int envId = preparer.persistedEnvironmentId();

        final DataSetBuilder b = new DataSetBuilder(true);
        b.newRow("server").with("id", 150).with("environment_id", envId).with("name", "s1").add();
        b.newRow("server").with("id", 151).with("environment_id", envId).with("name", "s2").with("statusUrl", "http://localhost").add();
        b.newRow("server").with("id", 152).with("environment_id", envId).with("name", "s3").with("statusUrl", "http://localhost:8080").add();

        preparer.insertMoreData(b.build());

        final List<String> servers = gateway.getImmutableSetOfObservableServers().stream()
                .map(ServerEntity::getName)
                .collect(toList());

        assertThat(servers).contains("s2", "s3").doesNotContain("s1");
    }
}
