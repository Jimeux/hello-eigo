package com.moobasoft.damego.ui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.moobasoft.damego.App;
import com.moobasoft.damego.R;
import com.moobasoft.damego.di.components.DaggerMainComponent;
import com.moobasoft.damego.di.modules.MainModule;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.EndlessOnScrollListener;
import com.moobasoft.damego.ui.PostsAdapter;
import com.moobasoft.damego.ui.presenters.IndexPresenter;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;
import static android.view.View.VISIBLE;

public class IndexActivity extends BaseActivity implements IndexPresenter.View, PostsAdapter.OnSummaryClickListener, SwipeRefreshLayout.OnRefreshListener {

    @Inject IndexPresenter presenter;

    @Bind(R.id.post_list)     RecyclerView postList;
    @Bind(R.id.swipe_refresh) SwipeRefreshLayout refreshLayout;
    @Bind(R.id.loading_view)  ViewGroup loadingView;
    @Bind(R.id.empty_view)    ViewGroup emptyView;
    @Bind(R.id.error_view)    ViewGroup errorView;
    @Bind({R.id.loading_view, R.id.error_view, R.id.empty_view, R.id.swipe_refresh})
    List<ViewGroup> listStateViews;

    private PostsAdapter postsAdapter;
    private LinearLayoutManager layoutManager;
    private String tagName;
    private List<Post> posts;

    private static final String POSTS_KEY = "posts";
    private static final String LAYOUT_KEY = "layout";
    private static final String CURRENT_PAGE_KEY = "current_page";
    private static final String PREVIOUS_TOTAL_KEY = "previous_total";
    public static final String TAG_NAME = "tag_name";

    private EndlessOnScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        initialiseInjector();
        presenter.bindView(this);

        tagName = getIntent().getStringExtra(TAG_NAME);
        posts = new ArrayList<>();
        initialiseRecyclerView();

        if (savedInstanceState == null) {
            activateView(R.id.loading_view);
            loadPosts();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putParcelable(LAYOUT_KEY, postList.getLayoutManager().onSaveInstanceState());
        state.putParcelable(POSTS_KEY, Parcels.wrap(posts));
        state.putInt(CURRENT_PAGE_KEY, scrollListener.getCurrentPage());
        state.putInt(PREVIOUS_TOTAL_KEY, scrollListener.getPreviousTotal());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle state) {
        super.onRestoreInstanceState(state);
        List<Post> savedPosts = Parcels.unwrap(state.getParcelable(POSTS_KEY));
        if (savedPosts != null) {
            postsAdapter.loadPosts(savedPosts);

            Parcelable savedRecyclerLayoutState = state.getParcelable(LAYOUT_KEY);
            if (savedRecyclerLayoutState != null)
                postList.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);

            int currentPage   = state.getInt(CURRENT_PAGE_KEY, scrollListener.getCurrentPage());
            int previousTotal = state.getInt(PREVIOUS_TOTAL_KEY, scrollListener.getPreviousTotal());
            scrollListener.restorePage(currentPage, previousTotal);

            activateView(R.id.swipe_refresh);
        }
    }

    private void loadPosts() {
        if (TextUtils.isEmpty(tagName)) {
            presenter.postsIndex(1);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(tagName);
            presenter.filterByTag(tagName, 1);
        }
    }

    // TODO: Move these two methods to new super class
    private void activateView(int id) {
        for (ViewGroup vg : listStateViews) vg.setVisibility(android.view.View.GONE);
        findViewById(id).setVisibility(VISIBLE);
    }

    @OnClick({R.id.refresh_btn1, R.id.refresh_btn2})
    public void clickRefresh() { onRefresh(); }

    @Override
    public void onDestroy() {
        presenter.releaseView();
        super.onDestroy();
    }

    private void initialiseInjector() {
        DaggerMainComponent.builder()
                .mainModule(new MainModule())
                .appComponent(((App) getApplication()).getAppComponent())
                .build().inject(this);
    }

    protected void initialiseRecyclerView() {
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorAccent);

        int orientation = getResources().getConfiguration().orientation;
        int columns = orientation == Configuration.ORIENTATION_PORTRAIT ? 1 : 2;
        postsAdapter = new PostsAdapter(this, posts, columns);
        layoutManager = new GridLayoutManager(this, columns);

        postList.setLayoutManager(layoutManager);
        postList.setAdapter(postsAdapter);
        scrollListener = new EndlessOnScrollListener(layoutManager, refreshLayout) {
            @Override
            public void onLoadMore(int page) {
                refreshLayout.setRefreshing(true);
                if (TextUtils.isEmpty(tagName))
                    presenter.postsIndex(page);
                else
                    presenter.filterByTag(tagName, page);
            }
        };
        postList.addOnScrollListener(scrollListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        boolean loggedIn = credentialStore.isLoggedIn();

        menu.findItem(R.id.action_logout).setVisible(loggedIn);
        menu.findItem(R.id.action_login).setVisible(!loggedIn);
        menu.findItem(R.id.action_register).setVisible(!loggedIn);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login:
                Intent loginIntent = new Intent(this, ConnectActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                loginIntent.putExtra(ConnectActivity.REGISTER, false);
                startActivity(loginIntent);
                break;

            case R.id.action_register:
                Intent registerIntent = new Intent(this, ConnectActivity.class);
                registerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                registerIntent.putExtra(ConnectActivity.REGISTER, true);
                startActivity(registerIntent);
                break;

            case R.id.action_logout:
                credentialStore.delete();
                Snackbar.make(toolbar, getString(R.string.logout_success), LENGTH_SHORT).show();
                break;
        }
        supportInvalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostsRetrieved(List<Post> posts) {
        if (scrollListener.getCurrentPage() == 1)
            postsAdapter.clear();
        refreshLayout.setRefreshing(false);
        postsAdapter.loadPosts(posts);

        activateView(R.id.swipe_refresh);

        //if (postsAdapter.isEmpty() && posts.isEmpty())
          //  activateView(R.id.error_view);
    } //TODO: No more posts message

    @Override
    public void onPostsError() {
        refreshLayout.setRefreshing(false);
        if(loadingView.getVisibility() == VISIBLE)
            activateView(R.id.error_view);
        Snackbar.make(toolbar, "Error getting posts.", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onSummaryClicked(Post post) {
        Intent i = new Intent(IndexActivity.this, ShowActivity.class);
        i.putExtra(ShowActivity.POST_ID, post.getId());
        startActivity(i);
    }

    @Override
    public void onRefresh() {
        if (errorView.getVisibility() == VISIBLE || emptyView.getVisibility() == VISIBLE) {
            refreshLayout.setRefreshing(false);
            activateView(R.id.loading_view);
        } else
            refreshLayout.setRefreshing(true);

        scrollListener.reset();
        loadPosts();
    }

}