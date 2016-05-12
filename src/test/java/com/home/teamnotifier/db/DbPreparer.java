package com.home.teamnotifier.db;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.builder.DataSetBuilder;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.SQLException;

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

    private Connection extractConnection(final EntityManager em) {
        return ((SessionImpl) (em.getDelegate())).connection();
    }

    void initDataBase() {
        transactionHelper.transaction(em -> {
            final Connection c = extractConnection(em);
            tryFillDb(c);
            return null;
        });
    }

    void insertMoreData(final IDataSet dataSet) {
        transactionHelper.transaction(em -> {
            final Connection c = extractConnection(em);
            tryInsertMore(c, dataSet);
            return null;
        });
    }

    private void tryInsertMore(final Connection c, final IDataSet dataSet) {
        try {
            DatabaseOperation.INSERT.execute(buildConnection(c), dataSet);
        } catch (DatabaseUnitException | SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private void tryFillDb(final Connection c) {
        try {
            DatabaseOperation.CLEAN_INSERT.execute(buildConnection(c), buildDataset());
        } catch (DatabaseUnitException | SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private DatabaseConnection buildConnection(final Connection c) throws DatabaseUnitException {
        final DatabaseConnection connection = new DatabaseConnection(c, "teamnotifier");

        final DatabaseConfig dbConfig = connection.getConfig();
        dbConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());
        dbConfig.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

        return connection;
    }

    TransactionHelper getTransactionHelper() {
        return transactionHelper;
    }
}
