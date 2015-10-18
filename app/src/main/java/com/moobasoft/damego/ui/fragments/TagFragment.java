package com.moobasoft.damego.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moobasoft.damego.R;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.EndlessOnScrollListener;
import com.moobasoft.damego.ui.PostsAdapter;
import com.moobasoft.damego.ui.activities.IndexActivity;
import com.moobasoft.damego.ui.presenters.IndexPresenter;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;

public class TagFragment extends BaseFragment
        implements IndexPresenter.View, SwipeRefreshLayout.OnRefreshListener, AppBarLayout.OnOffsetChangedListener {

    public static final String POSTS_KEY    = "posts";
    public static final String LAYOUT_KEY   = "layout";
    public static final String SCROLL_KEY   = "scroll_key";
    public static final String TAG_NAME     = "tag_name";
    public static final String SHOW_ALL_TAG = "すべて";

    private PostsAdapter postsAdapter;
    private LinearLayoutManager layoutManager;
    /** The tag name of the posts to be loaded. */
    private String tagName;
    /** OnScrollListener for {@code postsRecyclerView} */
    private EndlessOnScrollListener scrollListener;
    /** A reference to the Activity's AppBarLayout. */
    private AppBarLayout appBarLayout;
    /**
     *  Manually keep track of {@code appBarLayout}'s expanded/collapsed state.
     *  Makes use of {@code AppBarLayout.OnOffsetChangedListener}.
     */
    private boolean appBarIsExpanded = true;

    @Inject IndexPresenter presenter;

    @Bind(R.id.post_recycler) RecyclerView postsRecyclerView;
    @Bind(R.id.swipe_refresh) SwipeRefreshLayout refreshLayout;

    public TagFragment() {}

    public static TagFragment newInstance(String tagName) {
        TagFragment fragment = new TagFragment();
        Bundle args = new Bundle();
        args.putString(TAG_NAME, tagName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        tagName = getArguments().getString(TAG_NAME);
        int columns = getResources().getInteger(R.integer.main_list_columns);

        layoutManager = new GridLayoutManager(getActivity(), columns);
        postsAdapter = new PostsAdapter((IndexActivity)getActivity(), columns,
                getResources().getBoolean(R.bool.show_feature_views));
        scrollListener = new EndlessOnScrollListener(layoutManager) {
            @Override public void onLoadMore(int currentPage) {
                loadPosts(currentPage);
            }
            @Override public boolean isRefreshing() {
                setRefreshLayoutEnabled();
                return refreshLayout.isRefreshing();
            }
        };
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main, container, false);
        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar_layout);
        getComponent().inject(this);
        presenter.bindView(this);
        ButterKnife.bind(this, view);
        initialiseRecyclerView();
        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle state) {
        super.onViewStateRestored(state);

        if (state == null)
            onRefresh();
        else {
            List<Post> posts = Parcels.unwrap(state.getParcelable(POSTS_KEY));
            layoutManager.onRestoreInstanceState(state.getParcelable(LAYOUT_KEY));
            scrollListener.restoreState(Parcels.unwrap(state.getParcelable(SCROLL_KEY)));
            if (scrollListener.isFinished()) postsAdapter.setFinished();

            if (posts.isEmpty())
                activateEmptyView(getString(R.string.no_posts_found));
            else {
                postsAdapter.loadPosts(posts);
                activateContentView();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        appBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (layoutManager != null && scrollListener != null &&
            postsAdapter != null  && postsAdapter.getPostList() != null ) {
            state.putParcelable(LAYOUT_KEY, layoutManager.onSaveInstanceState());
            state.putParcelable(POSTS_KEY, Parcels.wrap(postsAdapter.getPostList()));
            state.putParcelable(SCROLL_KEY, Parcels.wrap(scrollListener.getOutState()));
        }
    }

    @Override
    public void onDestroyView() {
        presenter.releaseView();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    private void initialiseRecyclerView() {
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);

        postsRecyclerView.setLayoutManager(layoutManager);
        postsRecyclerView.setAdapter(postsAdapter);
        postsRecyclerView.addOnScrollListener(scrollListener);
    }

    private void setRefreshLayoutEnabled() {
        if (refreshLayout == null || postsRecyclerView == null) return;
        boolean canRefresh = appBarIsExpanded &&
                !ViewCompat.canScrollVertically(postsRecyclerView, -1);
        refreshLayout.setEnabled(canRefresh);
    }

    private void loadPosts(int page) {
        if (page == 1) activateLoadingView();
        refreshLayout.setRefreshing(true);

        if (tagName.equals(SHOW_ALL_TAG) || TextUtils.isEmpty(tagName))
            presenter.postsIndex(page);
        else
            presenter.filterByTag(tagName, page);
    }

    @Override
    public void onPostsRetrieved(List<Post> newPosts) {
        if (scrollListener.getCurrentPage() == 1)
            postsAdapter.clear();
        refreshLayout.setRefreshing(false);

        if (postsAdapter.isEmpty() && newPosts.isEmpty())
            activateEmptyView(getString(R.string.no_posts_found));
        else if (!postsAdapter.isEmpty() && newPosts.isEmpty()) {
            scrollListener.setFinished();
            postsAdapter.setFinished();
        } else {
            activateContentView();
            postsAdapter.loadPosts(newPosts);
        }
    }

    @Override
    public void onError(String message) {
        refreshLayout.setRefreshing(false);
        if (loadingView.getVisibility() == VISIBLE || errorView.getVisibility() == VISIBLE) {
            errorMessage.setText(message);
            activateView(R.id.error_view);
        } else
            Snackbar.make(postsRecyclerView, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        if (errorView.getVisibility() == VISIBLE || emptyView.getVisibility() == VISIBLE)
            activateLoadingView();
        else
            refreshLayout.setRefreshing(true);

        scrollListener.reset();
        loadPosts(1);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        appBarIsExpanded = verticalOffset == 0;
        setRefreshLayoutEnabled();
    }
}