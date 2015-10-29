package com.moobasoft.damego.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public class ToolbarFragment extends BaseFragment {

    public static final String TOOLBAR_KEY = "toolbar_key";

    public ToolbarFragment() {}

    public static ToolbarFragment newInstance(int toolbarResId) {
        ToolbarFragment fragment = new ToolbarFragment();
        Bundle args = new Bundle();
        args.putInt(TOOLBAR_KEY, toolbarResId);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle state) {
        int toolbarResId = getArguments().getInt(TOOLBAR_KEY);
        View view = inflater.inflate(toolbarResId, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
