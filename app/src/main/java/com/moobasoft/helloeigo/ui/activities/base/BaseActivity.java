package com.moobasoft.helloeigo.ui.activities.base;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.moobasoft.helloeigo.App;
import com.moobasoft.helloeigo.CredentialStore;
import com.moobasoft.helloeigo.R;
import com.moobasoft.helloeigo.di.components.DaggerMainComponent;
import com.moobasoft.helloeigo.di.components.MainComponent;
import com.moobasoft.helloeigo.di.modules.MainModule;
import com.moobasoft.helloeigo.ui.activities.ConnectActivity;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BaseActivity extends AppCompatActivity {

    @Inject
    protected CredentialStore credentialStore;

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