package com.moobasoft.damego.di.modules;

import com.moobasoft.damego.di.scopes.Endpoint;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class EndpointModule {

    private static final String PRODUCTION_API_URL = "http://10.4.105.2:3000/";

    @Endpoint
    @Provides
    @Singleton
    String provideEndpoint() { return PRODUCTION_API_URL; }

}