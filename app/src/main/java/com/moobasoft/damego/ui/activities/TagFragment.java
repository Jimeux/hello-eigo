package com.moobasoft.damego.ui.activities;

import android.os.Bundle;
import android.os.Parcelable;
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

import static android.view.View.VISIBLE;

public class TagFragment extends BaseFragment
        implements IndexPresenter.View, SwipeRefreshLayout.OnRefreshListener, AppBarLayout.OnOffsetChangedListener {

    public static final String POSTS_KEY          = "posts";
    public static final String LAYOUT_KEY         = "layout";
    public static final String CURRENT_PAGE_KEY   = "current_page";
    public static final String PREVIOUS_TOTAL_KEY = "previous_total";
    public static final String TAG_NAME           = "tag_name";
    public static final String SHOW_ALL_TAG       = "すべて";

    private PostsAdapter postsAdapter;
    private String tagName;
    private List<Post> posts;
    private EndlessOnScrollListener scrollListener;

    @Inject IndexPresenter presenter;

    @Bind(R.id.post_list)     RecyclerView postList;
    @Bind(R.id.swipe_refresh) SwipeRefreshLayout refreshLayout;
    private AppBarLayout appBarLayout;

    public TagFragment() {}

    public static TagFragment newInstance(String tagName) {
        TagFragment fragment = new TagFragment();
        Bundle args = new Bundle();
        args.putString(TAG_NAME, tagName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.content_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        initialiseInjector();
        presenter.bindView(this);
        posts = new ArrayList<>();
        tagName = getArguments().getString(TAG_NAME);

        initialiseRecyclerView();

        if (savedInstanceState == null) {
            activateLoadingView();
            loadPosts(1);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (postList == null || posts == null || scrollListener == null)
            return;
        state.putParcelable(LAYOUT_KEY, postList.getLayoutManager().onSaveInstanceState());
        state.putParcelable(POSTS_KEY, Parcels.wrap(posts));
        state.putInt(CURRENT_PAGE_KEY, scrollListener.getCurrentPage());
        state.putInt(PREVIOUS_TOTAL_KEY, scrollListener.getPreviousTotal());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle state) {
        super.onViewStateRestored(state);
        if (state == null) return;
        List<Post> savedPosts = Parcels.unwrap(state.getParcelable(POSTS_KEY));

        if (savedPosts != null && savedPosts.isEmpty())
            activateEmptyView(getString(R.string.no_posts_found));
        else if (savedPosts != null) {
            postsAdapter.loadPosts(savedPosts);
            // Restore RecyclerView's state
            Parcelable savedRecyclerLayoutState = state.getParcelable(LAYOUT_KEY);
            if (savedRecyclerLayoutState != null)
                postList.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
            // Restore RecyclerView's scroll listener's state
            int currentPage   = state.getInt(CURRENT_PAGE_KEY, scrollListener.getCurrentPage());
            int previousTotal = state.getInt(PREVIOUS_TOTAL_KEY, scrollListener.getPreviousTotal());
            scrollListener.restorePage(currentPage, previousTotal);

            activateContentView();
        }
        else onRefresh();
    }

    private void loadPosts(int page) {
        if (tagName.equals(SHOW_ALL_TAG) || TextUtils.isEmpty(tagName)) {
            presenter.postsIndex(page);
        } else {
            presenter.filterByTag(tagName, page);
        }
    }

    @Override public void onDestroyView() {
        presenter.releaseView();
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void initialiseInjector() {
        DaggerMainComponent.builder()
                .mainModule(new MainModule())
                .appComponent(((App) getActivity().getApplication()).getAppComponent())
                .build().inject(this);
    }

    protected void initialiseRecyclerView() {
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);

        int columns = getResources().getInteger(R.integer.main_list_columns);
        postsAdapter = new PostsAdapter((IndexActivity)getActivity(), posts, columns);
        LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), columns);

        postList.setLayoutManager(layoutManager);
        postList.setAdapter(postsAdapter);

        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(this);

        scrollListener = new EndlessOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                refreshLayout.setRefreshing(true);
                loadPosts(currentPage);
            }
            @Override
            public boolean isRefreshing() {
                setRefreshLayoutEnabled();
                return refreshLayout.isRefreshing();
            }
        };
        postList.addOnScrollListener(scrollListener);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        setRefreshLayoutEnabled();
    }

    private void setRefreshLayoutEnabled() {
        if (refreshLayout == null || postList == null) return;
        boolean cannotScrollUp =
                (appBarLayout.getHeight() - appBarLayout.getBottom()) <= 3 &&
                        !ViewCompat.canScrollVertically(postList, -1);
        refreshLayout.setEnabled(cannotScrollUp);
    }

    @Override
    public void onPostsRetrieved(List<Post> newPosts) {
        if (scrollListener.getCurrentPage() == 1)
            postsAdapter.clear();
        refreshLayout.setRefreshing(false);
        postsAdapter.loadPosts(newPosts);

        if (posts.isEmpty() && newPosts.isEmpty())
            activateEmptyView(getString(R.string.no_posts_found));
        else
            activateContentView();

        if (posts.size() > 0 && newPosts.isEmpty());
            // Activate footer?
    }

    @Override
    public void promptForLogin() {
        ((BaseActivity)getActivity()).promptForLogin();
    }

    @Override
    public void onError(String message) {
        refreshLayout.setRefreshing(false);
        if(loadingView.getVisibility() == VISIBLE || errorView.getVisibility() == VISIBLE) {
            errorMessage.setText(message);
            activateView(R.id.error_view);
        } else
            Snackbar.make(postList, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        if (errorView.getVisibility() == VISIBLE || emptyView.getVisibility() == VISIBLE) {
            refreshLayout.setRefreshing(false);
            activateView(R.id.loading_view);
        } else
            refreshLayout.setRefreshing(true);

        scrollListener.reset();
        loadPosts(1);
    }
}