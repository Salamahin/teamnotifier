package com.home.teamnotifier;

import com.google.common.io.Resources;
import com.google.inject.Injector;
import com.home.teamnotifier.db.AppServerEntity;
import com.home.teamnotifier.db.EnvironmentEntity;
import com.home.teamnotifier.db.TransactionHelper;

public class NotifierApplicationRunner {

    public static void main(String[] args)
            throws Exception {
        final String yamlPath = Resources.getResource("web.yml").getFile();
        final NotifierApplication application = new NotifierApplication();
        application.run("server", yamlPath);

//        final Injector injector = Injection.INJECTION_BUNDLE.getInjector();
//        final TransactionHelper gt = injector.getInstance(TransactionHelper.class);
//
//        gt.transaction(em -> {
//            em.persist(getEuEnv("eu2"));
//            return null;
//        });

    }

    private static EnvironmentEntity getEuEnv(final String name) {
        final EnvironmentEntity eu = new EnvironmentEntity(name);
        final AppServerEntity sst = eu.newAppServer("sst", "http://google.com");
        final AppServerEntity wfa = eu.newAppServer("wfa");
        eu.newAppServer("wfe");

        wfa.newSharedResource("soapb");

        sst.newSharedResource("abr_tivu");
        sst.newSharedResource("aei_sks");
        sst.newSharedResource("aei_default");
        sst.newSharedResource("archiv_process");
        sst.newSharedResource("flexprod");
        sst.newSharedResource("kez_dwhacl");
        sst.newSharedResource("mat_psl");
        sst.newSharedResource("nsu_bse2e");
        sst.newSharedResource("para_process");
        sst.newSharedResource("pzt_ffm");
        sst.newSharedResource("prd_ffm");

        return eu;
    }
}