package com.moobasoft.damego.rest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.moobasoft.damego.CredentialStore;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;


public final class ApiHeaders implements Interceptor {

    private final Context context;
    private final CredentialStore credentialStore;
    public static final int MAX_STALE = 60 * 60 * 24 * 28;

    public ApiHeaders(Context context, CredentialStore credentialStore) {
        this.context = context;
        this.credentialStore = credentialStore;
    }

    @Override
    public Response intercept(Chain chain) {
        try {
            Request.Builder builder = chain.request().newBuilder();
            addAuthHeader(builder);
            addCacheHeader(builder);
            Request request = builder
                    .header("Accept", "application/javascript, application/json")
                    .build();
            return chain.proceed(request);
        } catch (IOException e) {
            return null;
        }
    }

    private void addCacheHeader(Request.Builder builder) {
        if (!isOnline())
            builder.header("Cache-Control", "public, only-if-cached, max-stale=" + MAX_STALE);
    }

    private void addAuthHeader(Request.Builder builder) {
        String token = credentialStore.getAccessToken();
        if (token != null) builder.header("Authorization", "Bearer " + token);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}