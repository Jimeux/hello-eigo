package com.moobasoft.damego.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moobasoft.damego.R;

import butterknife.ButterKnife;

public class BookmarksFragment extends BaseFragment  {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        ButterKnife.bind(this, view);
        onRefresh();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.removeGroup(R.id.fragment_specific_options);
    }

    @Override
    public void setToolbar() {
        if (toolbar == null) return;
        toolbar.setTitle(getString(R.string.bookmarks_title));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        Fragment content = getChildFragmentManager().findFragmentById(contentView.getId());
        if (content == null) {
            TagFragment tagFragment = TagFragment.newInstance(TagFragment.MODE_BOOKMARKS, null);
            getChildFragmentManager().beginTransaction()
                    .add(contentView.getId(), tagFragment)
                    .commit();
        }
        activateContentView();
    }
}