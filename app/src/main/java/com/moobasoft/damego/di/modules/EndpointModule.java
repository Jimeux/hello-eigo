package com.moobasoft.damego.di.modules;

import com.moobasoft.damego.di.scopes.Endpoint;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class EndpointModule {

    //private static final String PRODUCTION_API_URL = "http://10.4.139.157:3000/";
    //private static final String PRODUCTION_API_URL = "http://192.168.11.5:3000/";
    private static final String PRODUCTION_API_URL = "http://210.140.83.8/";

    @Endpoint
    @Provides
    @Singleton
    String provideEndpoint() { return PRODUCTION_API_URL; }

}