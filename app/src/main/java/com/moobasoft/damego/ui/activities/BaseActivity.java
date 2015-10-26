package com.moobasoft.damego.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.moobasoft.damego.App;
import com.moobasoft.damego.CredentialStore;
import com.moobasoft.damego.R;
import com.moobasoft.damego.di.components.DaggerMainComponent;
import com.moobasoft.damego.di.components.MainComponent;
import com.moobasoft.damego.di.modules.MainModule;

import javax.inject.Inject;

import butterknife.Bind;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseActivity extends AppCompatActivity {

    @Inject CredentialStore credentialStore;

    @Nullable @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    protected MainComponent getComponent() {
        return DaggerMainComponent.builder()
                .mainModule(new MainModule())
                .appComponent(((App) getApplication()).getAppComponent())
                .build();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    protected boolean isLoggedIn() {
        return credentialStore.isLoggedIn();
    }

    public void doIfLoggedIn(Intent intent) {
        if (isLoggedIn())
            startActivity(intent);
        else
            promptForLogin();
    }

    public void promptForLogin() {
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_unauthorized), Snackbar.LENGTH_LONG)
                .setActionTextColor(getResources().getColor(R.color.green400))
                .setAction(getString(R.string.login), v -> {
                    Intent intent = new Intent(getApplicationContext(), ConnectActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(ConnectActivity.REGISTER, false);
                    startActivity(intent);
                })
                .show();
    }
}