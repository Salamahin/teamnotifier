package com.home.teamnotifier;

import com.google.common.io.Resources;

class NotifierApplicationRunner {
    public static void main(String[] args) throws Exception {
        final String yamlPath = Resources.getResource("web.yml").getFile();
        final NotifierApplication application = new NotifierApplication();
        application.run("server", yamlPath);
    }
}