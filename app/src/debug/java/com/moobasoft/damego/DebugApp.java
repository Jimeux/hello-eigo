package com.moobasoft.damego;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;

public class DebugApp extends App {

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());

        LeakCanary.install(this);
    }
}