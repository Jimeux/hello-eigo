package com.moobasoft.damego.ui.fragments;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moobasoft.damego.App;
import com.moobasoft.damego.R;
import com.moobasoft.damego.di.components.DaggerMainComponent;
import com.moobasoft.damego.di.components.MainComponent;
import com.moobasoft.damego.di.modules.MainModule;
import com.moobasoft.damego.ui.activities.BaseActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static android.view.View.VISIBLE;

public abstract class RxFragment extends BaseFragment {

    @Bind(R.id.content)       ViewGroup contentView;
    @Bind(R.id.loading_view)  ViewGroup loadingView;
    @Bind(R.id.empty_view)    ViewGroup emptyView;
    @Bind(R.id.error_view)    ViewGroup errorView;
    @Bind(R.id.error_msg)     TextView errorMessage;
    @Bind(R.id.empty_msg)     TextView emptyMessage;
    @Bind({R.id.loading_view, R.id.error_view, R.id.empty_view, R.id.content})
    List<ViewGroup> stateViews;

    public void onError(int messageId) {
        String message = getString(messageId);
        if (message == null)
            message = getString(R.string.error_default);
        activateErrorView(message);
    }

    public void promptForLogin() {
        ((BaseActivity) getActivity()).promptForLogin();
    }

    protected MainComponent getComponent() {
        return DaggerMainComponent.builder()
                .mainModule(new MainModule())
                .appComponent(((App) getActivity().getApplication()).getAppComponent())
                .build();
    }

    protected void activateView(View view) {
        for (ViewGroup vg : stateViews)
            vg.setVisibility(android.view.View.GONE);

        if (view == null)
            activateErrorView(getString(R.string.error_default));
        else
            view.setVisibility(VISIBLE);
    }

    protected void activateEmptyView(String message) {
        activateView(emptyView);
        emptyMessage.setText(message);
    }

    protected void activateErrorView(String message) {
        if (loadingView.getVisibility() == VISIBLE || errorView.getVisibility() == VISIBLE) {
            errorMessage.setText(message);
            activateView(errorView);
        } else
           Snackbar.make(getActivity().findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }

    protected void activateContentView() {
        activateView(contentView);
    }
    
    protected void activateLoadingView() {
        activateView(loadingView);
    }
    
    public abstract void onRefresh();

    @OnClick({R.id.empty_refresh_btn, R.id.error_refresh_btn})
    public void clickRefresh() {
        onRefresh();
    }

}