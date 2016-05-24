package com.home.teamnotifier.db;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.builder.DataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

import java.sql.SQLException;

import static com.home.teamnotifier.db.tools.ConnectionHepler.extract;

final class DbPreparer {

    private final TransactionHelper transactionHelper;

    DbPreparer() {
        transactionHelper = new TransactionHelper();
    }

    int persistedEnvironmentId() {
        return 1;
    }

    int persistedServerId() {
        return 11;
    }

    int persistedResourceId() {
        return 111;
    }

    int persistedUserId() {
        return 155;
    }

    String persistedUserName() {
        return "user1";
    }

    String persistedUserPassHash() {
        return "passHash1";
    }

    String persistedUserSalt() {
        return "salt1";
    }

    private IDataSet buildDataset() throws DataSetException {
        int envId = persistedEnvironmentId();
        int srvId = persistedServerId();

        final DataSetBuilder b = new DataSetBuilder(true);
        b.newRow("Environment").with("id", envId).with("name", "env1").add();
        b.newRow("Server").with("id", srvId).with("name", "srv1").with("environment_id", envId).add();
        b.newRow("Resource").with("id", persistedResourceId()).with("name", "app1").with("server_id", srvId).add();
        b.newRow("User").with("id", persistedUserId()).with("name", persistedUserName()).with("passHash", persistedUserPassHash()).with("salt", persistedUserSalt()).add();

        return b.build();
    }

    void initDataBase() {
        transactionHelper.transaction(em -> {
            tryFillDb(extract(em));
            return null;
        });
    }

    void insertMoreData(final IDataSet dataSet) {
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
            DatabaseOperation.CLEAN_INSERT.execute(c, buildDataset());
        } catch (DatabaseUnitException | SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    TransactionHelper getTransactionHelper() {
        return transactionHelper;
    }
}
