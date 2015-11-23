package com.home.teamnotifier;

import com.google.common.io.Resources;
import com.google.inject.Injector;
import com.home.teamnotifier.db.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.home.teamnotifier.utils.PasswordHasher.toMd5Hash;

public class NotifierApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotifierApplicationRunner.class);

  public static void main(String[] args)
  throws Exception {
      final String yamlPath = Resources.getResource("web.yml").getFile();
      final NotifierApplication application = new NotifierApplication();
      application.run("server", yamlPath);

      final Injector injector = Injection.INJECTION_BUNDLE.getInjector();
      final TransactionHelper gt = injector.getInstance(TransactionHelper.class);

      final EnvironmentEntity eu1 = gt.transaction(em -> em.merge(getEuEnv("eu1")));

      gt.transaction(em -> {
          em.persist(getEuEnv("eu2"));
          return null;
      });

//      final String userName = "user";
//      final String userPass = "pass";
//
//      final UserEntity actor = gt.transaction(em -> em.merge(new UserEntity(userName, toMd5Hash(userPass))));
//
//      final SharedResourceEntity resource = eu1.getImmutableListOfAppServers().stream()
//              .flatMap(s -> s.getImmutableListOfResources().stream())
//              .findFirst()
//              .get();
//
//      final List<ActionOnSharedResourceEntity> infos = uniqueInfosForResource(actor, resource);
//      gt.transaction(em -> {
//          for (ActionOnSharedResourceEntity info : infos)
//              em.merge(info);
//          return null;
//      });
//
//      LOGGER.info("Lots of actions generated for resource {} {} {}",
//              resource.getAppServer().getEnvironment().getName(),
//              resource.getAppServer().getName(),
//              resource.getName()
//      );
  }

  private static EnvironmentEntity getEuEnv(final String name) {
    final EnvironmentEntity eu = new EnvironmentEntity(name);
    final AppServerEntity sst = eu.newAppServer("sst");
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

  private static List<ActionOnSharedResourceEntity> uniqueInfosForResource(
          final UserEntity actor,
          final SharedResourceEntity resource
  ) {
    final List<ActionOnSharedResourceEntity> infos = new ArrayList<>();
    for(int i = 0; i<50; i++) {
      infos.add(new ActionOnSharedResourceEntity(actor, resource, "deploy"));
    }
    return infos;
  }
}