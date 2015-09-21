package com.moobasoft.damego.di.components;

import android.content.Context;

import com.moobasoft.damego.CredentialStore;
import com.moobasoft.damego.di.modules.AppModule;
import com.moobasoft.damego.di.modules.EndpointModule;
import com.moobasoft.damego.di.modules.RestModule;
import com.moobasoft.damego.di.scopes.Endpoint;

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