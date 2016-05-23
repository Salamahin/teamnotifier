package com.home.teamnotifier;

import com.google.inject.Guice;
import com.google.inject.Stage;
import com.hubspot.dropwizard.guice.GuiceBundle;

final class Injection {
    static final GuiceBundle<NotifierConfiguration> INJECTION_BUNDLE =
            GuiceBundle.<NotifierConfiguration>newBuilder()
                    .addModule(new NotifierModule())
                    .setConfigClass(NotifierConfiguration.class)
                    .enableAutoConfig(Injection.class.getPackage().getName())
                    .setInjectorFactory((stage, modules) -> Guice.createInjector(Stage.DEVELOPMENT, modules))
                    .build();

    private Injection() {
        throw new AssertionError();
    }
}
