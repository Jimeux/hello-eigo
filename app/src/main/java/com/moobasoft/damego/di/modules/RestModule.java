package com.moobasoft.damego.di.modules;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moobasoft.damego.CredentialStore;
import com.moobasoft.damego.di.scopes.Endpoint;
import com.moobasoft.damego.rest.ApiAuthenticator;
import com.moobasoft.damego.rest.ApiHeaders;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

import static java.util.concurrent.TimeUnit.SECONDS;

@Singleton
@Module
public class RestModule {

    private static final int DISK_CACHE_SIZE = 5 * 1024 * 1024; //5MB

    @Provides
    @Singleton
    Retrofit provideRetrofit(@Endpoint String baseUrl, Gson gson, Context context,
                             CredentialStore credentialStore) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        configureHttpClient(retrofit.client(), context, credentialStore);
        return retrofit;
    }

    @Provides @Singleton
    Gson provideGson() {
        final GsonBuilder gson = new GsonBuilder();
        gson.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        gson.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gson.create();
    }

    private static void configureHttpClient(OkHttpClient client, Context context,
                                            CredentialStore credentialStore) {
        client.interceptors().add(new ApiHeaders(context, credentialStore));
        client.setAuthenticator(new ApiAuthenticator(context, credentialStore));

        // TODO: Debug build only
        //client.networkInterceptors().add(new StethoInterceptor());

        client.setConnectTimeout(7, SECONDS);
        client.setReadTimeout(7, SECONDS);
        client.setWriteTimeout(7, SECONDS);

        // Install an HTTP cache in the application cache directory.
        File cacheDir = new File(context.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        //TODO: Reactivate
        //client.setCache(cache);
    }

}