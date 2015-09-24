package com.moobasoft.damego.ui.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moobasoft.damego.App;
import com.moobasoft.damego.CredentialStore;
import com.moobasoft.damego.R;
import com.moobasoft.damego.di.components.DaggerMainComponent;
import com.moobasoft.damego.di.components.MainComponent;
import com.moobasoft.damego.di.modules.MainModule;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

import static android.view.View.VISIBLE;

public abstract class BaseFragment extends Fragment {

    @Inject CredentialStore credentialStore;

    protected MainComponent getComponent() {
        return DaggerMainComponent.builder()
                .mainModule(new MainModule())
                .appComponent(((App) getActivity().getApplication()).getAppComponent())
                .build();
    }

    protected boolean isLoggedIn() {
        return credentialStore.isLoggedIn();
    }

    protected void doIfLoggedIn(Intent intent) {
        if (isLoggedIn()) {
            startActivity(intent);
        } else {
            promptForLogin();
        }
    }

    public void promptForLogin() {
        /*Snackbar.make(toolbar, getString(R.string.unauthorized), LENGTH_INDEFINITE)
                .setActionTextColor(getResources().getColor(R.color.green400))
                .setAction(getString(R.string.login), v -> {
                    Intent intent = new Intent(getActivity().getApplicationContext(), ConnectActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(ConnectActivity.REGISTER, false);
                    startActivity(intent);
                })
                .show();*/
    }

    @Bind(R.id.loading_view)  ViewGroup loadingView;
    @Bind(R.id.empty_view)    ViewGroup emptyView;
    @Bind(R.id.error_view)    ViewGroup errorView;
    @Bind(R.id.error_msg)     TextView errorMessage;
    @Bind(R.id.empty_msg)     TextView emptyMessage;
    @Bind({R.id.loading_view, R.id.error_view, R.id.empty_view, R.id.content})
    List<ViewGroup> stateViews;

    protected void activateView(int id) {
        for (ViewGroup vg : stateViews) vg.setVisibility(android.view.View.GONE);
        getView().findViewById(id).setVisibility(VISIBLE);
    }

    protected void activateEmptyView(String message) {
        activateView(R.id.empty_view);
        emptyMessage.setText(message);
    }
    
    protected void activateContentView() {
        activateView(R.id.content);
    }
    
    protected void activateLoadingView() {
        activateView(R.id.loading_view);
    }
    
    protected void activateErrorView(String message) {
        activateView(R.id.loading_view);
        errorMessage.setText(message);
    }
    
    public abstract void onRefresh();

    @OnClick({R.id.empty_refresh_btn, R.id.error_refresh_btn})
    public void clickRefresh() { onRefresh(); }
}