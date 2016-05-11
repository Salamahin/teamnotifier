package com.home.teamnotifier;

import com.google.common.io.Resources;
import com.google.inject.Injector;
import com.home.teamnotifier.db.TransactionHelper;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.internal.SessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

public class NotifierApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotifierApplicationRunner.class);

    public static void main(String[] args) throws Exception {

        final String yamlPath = Resources.getResource("web.yml").getFile();
        final NotifierApplication application = new NotifierApplication();
        application.run("server", yamlPath);

        final Injector injector = Injection.INJECTION_BUNDLE.getInjector();
        final TransactionHelper gt = injector.getInstance(TransactionHelper.class);


        try(final InputStream is = NotifierApplicationRunner.class.getResourceAsStream("/dataset.xml")) {
            gt.transaction(em -> {
                final Connection c = ((SessionImpl)(em.getDelegate())).connection();
                fillDb(is, c);
                return null;
            });
        }
    }

    private static void fillDb(final InputStream input, final Connection c) {
        try {
            final DatabaseConnection connection = new DatabaseConnection(c, "teamnotifier");
            final DatabaseConfig dbConfig = connection.getConfig();
            dbConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());
            dbConfig.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);
            final IDataSet dataSet = new FlatXmlDataSetBuilder().build(input);

            DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
        } catch (DatabaseUnitException | SQLException e) {
            LOGGER.error("Failed to fill DB", e);
        }
    }
}