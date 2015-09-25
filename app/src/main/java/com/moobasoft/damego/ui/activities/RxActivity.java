package com.moobasoft.damego.ui.activities;

import android.view.ViewGroup;
import android.widget.TextView;

import com.moobasoft.damego.R;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static android.view.View.VISIBLE;

public abstract class RxActivity extends BaseActivity {

    @Bind(R.id.loading_view)  ViewGroup loadingView;
    @Bind(R.id.empty_view)    ViewGroup emptyView;
    @Bind(R.id.error_view)    ViewGroup errorView;
    @Bind(R.id.error_msg)     TextView errorMessage;
    @Bind({R.id.loading_view, R.id.error_view, R.id.empty_view, R.id.content})
    List<ViewGroup> stateViews;

    protected void activateView(int id) {
        for (ViewGroup vg : stateViews)
            vg.setVisibility(android.view.View.GONE);

        findViewById(id).setVisibility(VISIBLE);
    }

    public abstract void onRefresh();

    @OnClick({R.id.empty_refresh_btn, R.id.error_refresh_btn})
    public void clickRefresh() { onRefresh(); }
}