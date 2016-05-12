package com.home.teamnotifier;

import com.google.common.collect.Range;
import com.home.teamnotifier.db.ActionRawDataProvider;
import com.home.teamnotifier.db.TransactionHelper;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.builder.DataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.home.teamnotifier.db.tools.ConnectionHepler.extract;

public class FunctionalityTestDataFiller {

    private final int userId = 1223;
    private final int resourceId = 111;
    private final int serverId = 21;

    private final TransactionHelper transactionHelper;

    private final ActionRawDataProvider serverActionDataProvider;
    private final ActionRawDataProvider resourceActionDataProvider;

    public FunctionalityTestDataFiller(TransactionHelper transactionHelper) {
        this.transactionHelper = transactionHelper;

        serverActionDataProvider = new ActionRawDataProvider("ServerAction", "server_id");
        resourceActionDataProvider = new ActionRawDataProvider("ResourceAction", "resource_id");
    }

    public void fillDb() {
        initDataBase();
        addActionsToDataBase();
    }

    private void addActionsToDataBase() {
        final Range<Instant> timeRange = Range.closed(
                Instant.now().minus(10, ChronoUnit.DAYS),
                Instant.now()
        );

        try {
            insertMoreData(serverActionDataProvider.newActionPerHour(userId, serverId,timeRange));
            insertMoreData(resourceActionDataProvider.newActionPerHour(userId, resourceId,timeRange));
        } catch (DataSetException e) {
           throw new IllegalStateException(e);
        }
    }

    private IDataSet getData() throws DataSetException {
        final DataSetBuilder b = new DataSetBuilder(true);

        b.newRow("Environment").with("id", 1).with("name", "eu1").add();
        b.newRow("Server").with("id", 11).with("name", "sst").with("environment_id", 1).add();
        b.newRow("Resource").with("id", resourceId).with("name", "abr_tivu").with("server_id", 11).add();
        b.newRow("Resource").with("id", 112).with("name", "aei_sks").with("server_id", 11).add();
        b.newRow("Resource").with("id", 113).with("name", "archiv_process").with("server_id", 11).add();
        b.newRow("Resource").with("id", 114).with("name", "flexprod").with("server_id", 11).add();
        b.newRow("Resource").with("id", 115).with("name", "kez_dwhacl").with("server_id", 11).add();
        b.newRow("Resource").with("id", 116).with("name", "mat_psl").with("server_id", 11).add();
        b.newRow("Resource").with("id", 117).with("name", "nsu_bse2e").with("server_id", 11).add();
        b.newRow("Resource").with("id", 118).with("name", "para_process").with("server_id", 11).add();
        b.newRow("Resource").with("id", 119).with("name", "prd_ffm").with("server_id", 11).add();
        b.newRow("Resource").with("id", 120).with("name", "pzt_ffm").with("server_id", 11).add();
        b.newRow("Server").with("id", 12).with("name", "wfa").with("environment_id", 1).add();
        b.newRow("Resource").with("id", 122).with("name", "soapb").with("server_id", 12).add();
        b.newRow("Server").with("id", 13).with("name", "wfe").with("environment_id", 1).add();
        b.newRow("Resource").with("id", 131).with("name", "apps").with("server_id", 13).add();

        b.newRow("Environment").with("id", 2).with("name", "eu2").add();
        b.newRow("Server").with("id", serverId).with("name", "sst").with("environment_id", 2).add();
        b.newRow("Server").with("id", 22).with("name", "wfa").with("environment_id", 2).add();
        b.newRow("Server").with("id", 23).with("name", "wfe").with("environment_id", 2).add();
        b.newRow("Server").with("id", 24).with("name", "ord").with("environment_id", 2).add();

        b.newRow("User").with("id", userId).with("name", "user1").with("passHash", "passHash").with("salt", "salt").add();

        return b.build();
    }

    private void initDataBase() {
        transactionHelper.transaction(em -> {
            tryFillDb(extract(em));
            return null;
        });
    }

    private void insertMoreData(final IDataSet dataSet) {
        transactionHelper.transaction(em -> {
            tryInsertMore(extract(em), dataSet);
            return null;
        });
    }

    private void tryInsertMore(final DatabaseConnection c, final IDataSet dataSet) {
        try {
            DatabaseOperation.INSERT.execute(c, dataSet);
        } catch (DatabaseUnitException | SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private void tryFillDb(final DatabaseConnection c) {
        try {
            DatabaseOperation.CLEAN_INSERT.execute(c, getData());
        } catch (DatabaseUnitException | SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
