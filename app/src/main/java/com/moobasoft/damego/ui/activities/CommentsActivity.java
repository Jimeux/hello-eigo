package com.moobasoft.damego.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.moobasoft.damego.App;
import com.moobasoft.damego.R;
import com.moobasoft.damego.di.components.DaggerMainComponent;
import com.moobasoft.damego.di.modules.MainModule;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.CommentsAdapter;
import com.moobasoft.damego.ui.EndlessOnScrollListener;
import com.moobasoft.damego.ui.presenters.ShowPresenter;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.moobasoft.damego.ui.activities.ShowActivity.POST_ID;

public class CommentsActivity extends BaseActivity implements ShowPresenter.ShowView, SwipeRefreshLayout.OnRefreshListener {

    @Inject ShowPresenter presenter;

    @Bind(R.id.fab)           FloatingActionButton fab;
    @Bind(R.id.comment_list)  RecyclerView commentList;
    @Bind(R.id.swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @Bind({R.id.loading_view, R.id.error_view, R.id.empty_view, R.id.swipe_refresh})
    List<ViewGroup> listStateViews;

    private static final String LAYOUT_KEY = "layout";
    private CommentsAdapter commentsAdapter;
    private int postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initialiseRecyclerView();
        initialiseInjector();

        presenter.bindView(this);

        if (savedInstanceState != null)
            postId = savedInstanceState.getInt(POST_ID, -1);

        if (postId <= 0)
            postId = getIntent().getIntExtra(POST_ID, -1);

        if (postId <= 0)
            onPostError();
        else {
            activateView(R.id.loading_view);
            presenter.getPost(postId);
        }
    }

    private void activateView(int id) {
        for (ViewGroup vg : listStateViews) vg.setVisibility(GONE);
        findViewById(id).setVisibility(VISIBLE);
    }

    private void initialiseInjector() {
        DaggerMainComponent.builder()
                .mainModule(new MainModule())
                .appComponent(((App)getApplication()).getAppComponent())
                .build().inject(this);
    }

    private void initialiseRecyclerView() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorAccent);
        commentsAdapter  = new CommentsAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        commentList.setLayoutManager(layoutManager);
        commentList.setAdapter(commentsAdapter);
        commentList.addOnScrollListener(new EndlessOnScrollListener(layoutManager, swipeRefreshLayout) {
            @Override
            public void onLoadMore(int currentPage) {
                //refreshLayout.setRefreshing(true);
                //presenter.getPost(currentPage); //TODO: Paginate comments
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(POST_ID, postId);
        state.putParcelable(LAYOUT_KEY, commentList.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle state) {
        super.onRestoreInstanceState(state);
        Parcelable savedRecyclerLayoutState = state.getParcelable(LAYOUT_KEY);
        commentList.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
    }

    @Override
    protected void onDestroy() {
        presenter.releaseView();
        super.onDestroy();
    }

    @Override
    public void onPostRetrieved(Post post) {
        setTitle(post.getTitle());
        fab.setVisibility(VISIBLE);
        swipeRefreshLayout.setRefreshing(false);

        if (post.getComments().isEmpty()) {
            activateView(R.id.empty_view);
        } else {
            commentsAdapter.loadComments(post.getComments());
            activateView(R.id.swipe_refresh);
        }
    }

    @Override
    public void onPostError() {
        swipeRefreshLayout.setRefreshing(false);
        activateView(R.id.error_view);
        Snackbar.make(toolbar, getString(R.string.error_list), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        commentsAdapter.clear();
        presenter.getPost(postId);
    }

    @OnClick(R.id.fab)
    public void clickFab() {
        Intent intent = new Intent(CommentsActivity.this, CreateCommentActivity.class);
        intent.putExtra(POST_ID, postId);
        doIfLoggedIn(intent);
    }

    @OnClick({R.id.refresh_btn1, R.id.refresh_btn2})
    public void clickRefresh() { onRefresh(); }
}