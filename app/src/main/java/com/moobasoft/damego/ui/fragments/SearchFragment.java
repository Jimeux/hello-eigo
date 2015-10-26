package com.moobasoft.damego.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.moobasoft.damego.R;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.presenters.IndexPresenter;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class SearchFragment extends BaseFragment implements IndexPresenter.View {

    @Inject IndexPresenter presenter;

    @Override
    public void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        /*menu.findItem(R.id.action_search).setVisible(false);*/
        MenuItem bookmarksItem = menu.findItem(R.id.action_bookmarks);
        if (bookmarksItem != null) bookmarksItem.setVisible(false);
    }

    @Override
    public void setToolbar() {
        if (toolbar != null) {
            toolbar.setTitle("");
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        getComponent().inject(this);
        presenter.bindView(this);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle state) {
        super.onViewStateRestored(state);
        activateContentView();
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
    }

    @Override
    public void onDestroyView() {
        presenter.releaseView();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onError(int messageId) {
        super.onError(messageId);
        Snackbar.make(getView().getRootView(), messageId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        activateLoadingView();
    }

    @Override public void onPostsRetrieved(List<Post> posts) {}
}