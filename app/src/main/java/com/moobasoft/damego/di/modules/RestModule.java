package com.moobasoft.damego.di.modules;

import android.content.Context;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moobasoft.damego.BuildConfig;
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

    private static final int DISK_CACHE_SIZE = 7 * 1024 * 1024; //7MB

    @Provides
    @Singleton
    Retrofit provideRetrofit(@Endpoint String baseUrl, Gson gson, Context context, CredentialStore store) {
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        configureHttpClient(retrofit.client(), context, store);
        return retrofit;
    }

    @Provides @Singleton
    Gson provideGson() {
        final GsonBuilder gson = new GsonBuilder();
        gson.setDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        gson.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gson.create();
    }

    private static void configureHttpClient(OkHttpClient client, Context context, CredentialStore store) {
        client.interceptors().add(new ApiHeaders(context, store));
        client.setAuthenticator(new ApiAuthenticator(store));

        if (BuildConfig.DEBUG)
            client.networkInterceptors().add(new StethoInterceptor());

        client.setConnectTimeout(10, SECONDS);
        client.setReadTimeout(10, SECONDS);
        client.setWriteTimeout(10, SECONDS);

        File cacheDir = new File(context.getCacheDir(), "http");
        Cache cache = new Cache(cacheDir, DISK_CACHE_SIZE);
        client.setCache(cache);
    }

}