package com.home.teamnotifier;

import com.google.common.io.Resources;
import com.home.teamnotifier.NotifierApplication;

public class ApplicationRunner
{
  public static void main(String[] args)throws Exception
  {
    final String yamlPath=Resources.getResource("web.yml").getFile();
    NotifierApplication.main(new String[]{"server", yamlPath});
  }
}