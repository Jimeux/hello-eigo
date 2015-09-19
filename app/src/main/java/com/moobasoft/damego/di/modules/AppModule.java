package com.moobasoft.damego.di.modules;

import android.content.Context;
import android.content.SharedPreferences;

import com.moobasoft.damego.App;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.MODE_PRIVATE;

@Module
public class AppModule {

    private final App application;
    private static final String PREFS = "credentials";

    public AppModule(App application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFS, MODE_PRIVATE);
    }

}