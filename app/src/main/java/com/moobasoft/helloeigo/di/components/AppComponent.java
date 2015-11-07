package com.moobasoft.helloeigo.di.components;

import android.content.Context;

import com.moobasoft.helloeigo.CredentialStore;
import com.moobasoft.helloeigo.di.modules.AppModule;
import com.moobasoft.helloeigo.di.modules.EndpointModule;
import com.moobasoft.helloeigo.di.modules.RestModule;
import com.moobasoft.helloeigo.di.scopes.Endpoint;

import javax.inject.Singleton;

import dagger.Component;
import retrofit.Retrofit;

@Singleton
@Component(
        modules = {
                AppModule.class,
                RestModule.class,
                EndpointModule.class
        }
)
public interface AppComponent {

    Context applicationContext();

    Retrofit retrofit();

    CredentialStore credentialStore();

    @Endpoint String endpoint();

}