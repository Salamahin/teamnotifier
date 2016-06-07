package com.home.teamnotifier;

import com.google.common.io.Resources;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.home.teamnotifier.db.TransactionHelper;
import org.eclipse.jetty.plus.annotation.Injection;

public class NotifierApplicationRunner {
    public static void main(String[] args) throws Exception {
        final String yamlPath = Resources.getResource("web.yml").getFile();
        final NotifierApplication application = new NotifierApplication();
        application.run("server", yamlPath);

        new FunctionalityTestDataFiller(application.getInjector().getInstance(TransactionHelper.class)).fillDb();
    }
}