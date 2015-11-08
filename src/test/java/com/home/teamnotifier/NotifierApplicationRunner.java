package com.home.teamnotifier;

import com.google.common.io.Resources;
import com.home.teamnotifier.db.Commons;

public class NotifierApplicationRunner {
  public static void main(String[] args)
  throws Exception {
//    Commons.createPersistedEnvironmentWithOneServerAndOneResource("env", "srv", "app");
//    Commons.createPersistedUser("hello", "world");

    final String yamlPath = Resources.getResource("web.yml").getFile();
    NotifierApplication.main(new String[]{"server", yamlPath});
  }
}