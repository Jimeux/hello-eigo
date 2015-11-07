package com.moobasoft.helloeigo.rest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.moobasoft.helloeigo.CredentialStore;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import static com.moobasoft.helloeigo.rest.Rest.ACCEPT_HEADER;
import static com.moobasoft.helloeigo.rest.Rest.ACCEPT_JSON;
import static com.moobasoft.helloeigo.rest.Rest.AUTHORIZATION_HEADER;
import static com.moobasoft.helloeigo.rest.Rest.BEARER;
import static com.moobasoft.helloeigo.rest.Rest.CACHE_CONTROL_HEADER;
import static com.moobasoft.helloeigo.rest.Rest.CACHE_ONLY_IF_CACHED;


public final class ApiHeaders implements Interceptor {

    private final Context context;
    private final CredentialStore credentialStore;

    public ApiHeaders(Context context, CredentialStore credentialStore) {
        this.context = context;
        this.credentialStore = credentialStore;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder builder = originalRequest.newBuilder();
        addAuthHeader(builder);
        addCacheHeader(builder, originalRequest.method());
        Request request = builder
                .header(ACCEPT_HEADER, ACCEPT_JSON)
                .build();

        return chain.proceed(request);
    }

    private void addCacheHeader(Request.Builder builder, String method) {
        if (!isOnline() && method.equals("GET"))
            builder.header(CACHE_CONTROL_HEADER, CACHE_ONLY_IF_CACHED);
    }

    private void addAuthHeader(Request.Builder builder) {
        String token = credentialStore.getAccessToken();
        if (token != null) builder.header(AUTHORIZATION_HEADER, BEARER + token);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}