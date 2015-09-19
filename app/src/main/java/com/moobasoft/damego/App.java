package com.moobasoft.damego;

import android.app.Application;

import com.moobasoft.damego.di.components.AppComponent;
import com.moobasoft.damego.di.components.DaggerAppComponent;
import com.moobasoft.damego.di.modules.AppModule;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class App extends Application {

    private AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        initialiseInjector();
        initialiseCalligraphy();
    }

    private void initialiseInjector() {
        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    private void initialiseCalligraphy() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Light.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    public AppComponent getAppComponent() {
        return component;
    }

    public void setComponent(AppComponent component) {
        this.component = component;
    }
}