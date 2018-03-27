package com.moobasoft.helloeigo.di.modules;

import com.moobasoft.helloeigo.di.scopes.Endpoint;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class EndpointModule {

    private static final String PRODUCTION_API_URL = "http://helloeigo.net/";

    @Endpoint
    @Provides
    @Singleton
    String provideEndpoint() { return PRODUCTION_API_URL; }

}