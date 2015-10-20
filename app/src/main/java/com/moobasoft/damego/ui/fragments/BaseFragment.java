package com.moobasoft.damego.ui.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
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

public abstract class BaseFragment extends Fragment {

    @Nullable @Bind(R.id.toolbar) Toolbar toolbar;

    @Bind(R.id.content)       ViewGroup contentView;
    @Bind(R.id.loading_view)  ViewGroup loadingView;
    @Bind(R.id.empty_view)    ViewGroup emptyView;
    @Bind(R.id.error_view)    ViewGroup errorView;
    @Bind(R.id.error_msg)     TextView errorMessage;
    @Bind(R.id.empty_msg)     TextView emptyMessage;
    @Bind({R.id.loading_view, R.id.error_view, R.id.empty_view, R.id.content})
    List<ViewGroup> stateViews;

    protected static void toggleVisibility(View... views) {
        for (View view : views) {
            if (view == null) return;
            boolean isVisible = view.getVisibility() == View.VISIBLE;
            view.setVisibility(isVisible ? View.INVISIBLE : View.VISIBLE);
        }
    }

    public void promptForLogin() {
        ((BaseActivity) getActivity()).promptForLogin();
    }

    @Nullable
    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbar() {}

    protected MainComponent getComponent() {
        return DaggerMainComponent.builder()
                .mainModule(new MainModule())
                .appComponent(((App) getActivity().getApplication()).getAppComponent())
                .build();
    }

    protected void activateView(int id) {
        for (ViewGroup vg : stateViews)
            vg.setVisibility(android.view.View.GONE);
        try {
            getView().findViewById(id).setVisibility(VISIBLE);
        } catch (NullPointerException e) {
            activateErrorView(getString(R.string.error_default));
        }
    }

    protected void activateEmptyView(String message) {
        activateView(R.id.empty_view);
        emptyMessage.setText(message);
    }

    protected void activateErrorView(String message) {
        activateView(R.id.error_view);
        errorMessage.setText(message);
    }

    protected void activateContentView() {
        activateView(R.id.content);
    }
    
    protected void activateLoadingView() {
        activateView(R.id.loading_view);
    }
    
    public abstract void onRefresh();

    @OnClick({R.id.empty_refresh_btn, R.id.error_refresh_btn})
    public void clickRefresh() {
        onRefresh();
    }
}