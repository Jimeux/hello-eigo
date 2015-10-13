package com.moobasoft.damego.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;

import com.moobasoft.damego.App;
import com.moobasoft.damego.R;
import com.moobasoft.damego.di.components.DaggerMainComponent;
import com.moobasoft.damego.di.modules.MainModule;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.CommentsAdapter;
import com.moobasoft.damego.ui.EndlessOnScrollListener;
import com.moobasoft.damego.ui.presenters.ShowPresenter;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import static android.view.View.VISIBLE;
import static com.moobasoft.damego.ui.fragments.ShowFragment.POST_ID_KEY;

public class CommentsActivity extends RxActivity implements ShowPresenter.ShowView, OnRefreshListener {

    @Inject ShowPresenter presenter;

    @Bind(R.id.fab)           FloatingActionButton fab;
    @Bind(R.id.comment_list)  RecyclerView commentList;
    @Bind(R.id.swipe_refresh) SwipeRefreshLayout refreshLayout;

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
            postId = savedInstanceState.getInt(POST_ID_KEY, -1);

        if (postId <= 0)
            postId = getIntent().getIntExtra(POST_ID_KEY, -1);

        if (postId <= 0)
            onError("No post ID given!");
        else {
            activateLoadingView();
            presenter.getPost(postId);
        }
    }

    private void initialiseInjector() {
        DaggerMainComponent.builder()
                .mainModule(new MainModule())
                .appComponent(((App)getApplication()).getAppComponent())
                .build().inject(this);
    }

    private void initialiseRecyclerView() {
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorAccent);
        commentsAdapter  = new CommentsAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        commentList.setLayoutManager(layoutManager);
        commentList.setAdapter(commentsAdapter);
        commentList.addOnScrollListener(new EndlessOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                //refreshLayout.setRefreshing(true);
                //presenter.getPost(currentPage); //TODO: Paginate comments
            }

            @Override  // TODO: Fix this duplicated code
            public boolean isRefreshing() {
                if (toolbar != null && commentList != null) {
                    boolean cannotScrollUp =
                            toolbar.getVisibility() == VISIBLE &&
                                    !ViewCompat.canScrollVertically(commentList, -1);
                    refreshLayout.setEnabled(cannotScrollUp);
                }
                return refreshLayout.isRefreshing();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(POST_ID_KEY, postId);
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
        refreshLayout.setRefreshing(false);

        if (post.getComments().isEmpty()) {
            activateEmptyView(getString(R.string.comments_empty));
        } else {
            if (commentsAdapter.getItemCount() == 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                ViewGroup rootView = (ViewGroup) findViewById(android.R.id.content);

                TransitionManager.beginDelayedTransition(rootView, new Slide());
                commentList.setVisibility(View.VISIBLE);
            }

            commentsAdapter.loadComments(post.getComments());
            activateContentView();
        }
    }

    @Override
    public void onError(String message) {
        refreshLayout.setRefreshing(false);
        if(loadingView.getVisibility() == VISIBLE || errorView.getVisibility() == VISIBLE)
            activateErrorView(message);
        else
            Snackbar.make(toolbar, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        commentsAdapter.clear();
        presenter.getPost(postId);
    }

    @OnClick(R.id.fab)
    public void clickFab() {
        Intent intent = new Intent(CommentsActivity.this, CreateCommentActivity.class);
        intent.putExtra(POST_ID_KEY, postId);
        doIfLoggedIn(intent);
    }
}