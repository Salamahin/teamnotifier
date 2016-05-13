package com.home.teamnotifier.db.tools;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.hibernate.internal.SessionImpl;

import javax.persistence.EntityManager;
import java.sql.Connection;

public final class ConnectionHepler {
    private ConnectionHepler() {
        throw new AssertionError();
    }

    private static Connection getJdbConnection(final EntityManager em) {
        return ((SessionImpl) (em.getDelegate())).connection();
    }

    public static DatabaseConnection extract(final EntityManager em) {
        final DatabaseConnection connection = newDatabaseConnection(em);

        final DatabaseConfig dbConfig = connection.getConfig();
        dbConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());
        dbConfig.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

        return connection;
    }

    private static DatabaseConnection newDatabaseConnection(EntityManager em) {
        final DatabaseConnection connection;
        try {
            connection = new DatabaseConnection(getJdbConnection(em), "teamnotifier");
        } catch (DatabaseUnitException e) {
            throw new IllegalStateException(e);
        }
        return connection;
    }
}
