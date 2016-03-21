package com.home.teamnotifier;

import com.google.common.io.Resources;
import com.google.inject.Injector;
import com.home.teamnotifier.db.ServerEntity;
import com.home.teamnotifier.db.EnvironmentEntity;
import com.home.teamnotifier.db.TransactionHelper;

public class NotifierApplicationRunner {

    public static void main(String[] args)
            throws Exception {

        final String yamlPath = Resources.getResource("web.yml").getFile();
        final NotifierApplication application = new NotifierApplication();
        application.run("server", yamlPath);

        final Injector injector = Injection.INJECTION_BUNDLE.getInjector();
        final TransactionHelper gt = injector.getInstance(TransactionHelper.class);

        gt.transaction(em -> {
            em.persist(getEuEnv("eu1"));
            return null;
        });

    }

    private static EnvironmentEntity getEuEnv(final String name) {
        final EnvironmentEntity eu = new EnvironmentEntity(name);
        final ServerEntity sst = eu.newServer("sst", "http://google.com");
        final ServerEntity wfa = eu.newServer("wfa");
        eu.newServer("wfe");

        wfa.newResource("soapb");
        sst.newResource("abr_tivu");
        sst.newResource("aei_sks");
        sst.newResource("aei_default");
        sst.newResource("archiv_process");
        sst.newResource("flexprod");
        sst.newResource("kez_dwhacl");
        sst.newResource("mat_psl");
        sst.newResource("nsu_bse2e");
        sst.newResource("para_process");
        sst.newResource("pzt_ffm");
        sst.newResource("prd_ffm");

        return eu;
    }
}