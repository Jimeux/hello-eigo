package com.moobasoft.damego.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moobasoft.damego.R;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.PostsAdapter;
import com.moobasoft.damego.ui.StaggeredScrollListener;
import com.moobasoft.damego.ui.activities.MainActivity;
import com.moobasoft.damego.ui.fragments.base.RxFragment;
import com.moobasoft.damego.ui.presenters.PostsPresenter;

import org.parceler.Parcels;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.view.View.VISIBLE;

public class PostsFragment extends RxFragment implements PostsPresenter.View,
        SwipeRefreshLayout.OnRefreshListener, AppBarLayout.OnOffsetChangedListener {

    public static final String UUID_KEY  = "uuid_key";
    public static final String POSTS_KEY  = "posts_key";
    public static final String SCROLL_KEY = "scroll_key";

    public static final String TAG_NAME_ARG = "tag_name";
    public static final String MODE_ARG     = "mode";

    public enum Mode { ALL, BOOKMARKS, TAG, SEARCH }

    private UUID presenterUuid;

    /** RecyclerView.Adapter implementation for {@code postsRecyclerView} */
    private PostsAdapter postsAdapter;

    /** The tag name of the posts to be loaded (includes search terms and 'Bookmarks'). */
    private String tagName;

    /** Determine if this instance is for tags, bookmarks, search or all posts. */
    private Mode mode;

    /** OnScrollListener for {@code postsRecyclerView} */
    private StaggeredScrollListener scrollListener;

    /** A reference to the current AppBarLayout. */
    private AppBarLayout appBarLayout;

    /**
     * Manually keep track of {@code appBarLayout}'s expanded/collapsed state.
     * Makes use of {@code AppBarLayout.OnOffsetChangedListener}.
     */
    private boolean appBarIsExpanded = true;

    @Inject PostsPresenter presenter;

    @Bind(R.id.post_recycler) RecyclerView postsRecyclerView;
    @Bind(R.id.swipe_refresh) SwipeRefreshLayout refreshLayout;

    public PostsFragment() {}

    public static PostsFragment newInstance(Mode mode, String tagName) {
        PostsFragment fragment = new PostsFragment();
        Bundle args = new Bundle();
        args.putSerializable(MODE_ARG, mode);
        args.putString(TAG_NAME_ARG, tagName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle state) {
        super.onCreate(state);
        tagName = getArguments().getString(TAG_NAME_ARG);
        mode = (Mode) getArguments().getSerializable(MODE_ARG);
        int columns = getResources().getInteger(R.integer.main_list_columns);
        postsAdapter   = new PostsAdapter((MainActivity)getActivity(), columns, tagName);
        scrollListener = new StaggeredScrollListener() {
            @Override public void onLoadMore(int currentPage) {
                loadPosts(currentPage, false);
            }
            @Override public boolean isRefreshing() {
                setRefreshLayoutEnabled();
                return refreshLayout.isRefreshing();
            }
        };
        restoreState(state);
    }

    private void restoreState(@Nullable Bundle state) {
        if (state != null) {
            List<Post> posts = Parcels.unwrap(state.getParcelable(POSTS_KEY));
            scrollListener.restoreState(Parcels.unwrap(state.getParcelable(SCROLL_KEY)));
            if (scrollListener.isFinished()) postsAdapter.setFinished();
            postsAdapter.loadPosts(posts);
        }
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tag, container, false);
        appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar);
        getComponent().inject(this);
        presenter.bindView(this);
        ButterKnife.bind(this, view);
        initialiseRecyclerView();
        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle state) {
        super.onViewStateRestored(state);

        if (state == null) { // Add first-init check
            loadPosts(1, false);
        } else {
            if (postsAdapter.getPostList().isEmpty())
                activateEmptyView(getString(R.string.no_posts_found));
            else
                activateContentView();
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
        state.putSerializable(UUID_KEY, presenterUuid);
        state.putParcelable(POSTS_KEY, Parcels.wrap(postsAdapter.getPostList()));
        state.putParcelable(SCROLL_KEY, Parcels.wrap(scrollListener.getOutState()));
    }

    @Override
    public void onDestroyView() {
        presenter.releaseView();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    private void initialiseRecyclerView() {
        int columns = getResources().getInteger(R.integer.main_list_columns);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(
                columns, StaggeredGridLayoutManager.VERTICAL);

        postsRecyclerView.setLayoutManager(layoutManager);
        postsRecyclerView.addOnScrollListener(scrollListener);
        postsRecyclerView.setAdapter(postsAdapter);
        scrollListener.setLayoutManager(layoutManager);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeResources(
                R.color.colorPrimary, R.color.colorAccent);
    }

    private void setRefreshLayoutEnabled() {
        if (refreshLayout == null || postsRecyclerView == null)
            return;
        boolean canRefresh = appBarIsExpanded &&
                !ViewCompat.canScrollVertically(postsRecyclerView, -1);
        refreshLayout.setEnabled(canRefresh);
    }

    private void loadPosts(int page, boolean refresh) {
        if (page == 1 || errorView.getVisibility() == VISIBLE || emptyView.getVisibility() == VISIBLE)
            activateLoadingView();
        else
            refreshLayout.setRefreshing(true);

        presenter.loadPosts(mode, tagName, refresh, page);
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

    // FIXME: Current page is incremented even on error, which may result in skipped pages
    @Override
    public void onError(int messageId) {
        super.onError(messageId);
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void promptForLogin() {
        super.promptForLogin();
        activateErrorView(getString(R.string.error_unauthorized));
    }

    @Override
    public void onRefresh() {
        scrollListener.reset();
        loadPosts(1, true);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        appBarIsExpanded = verticalOffset == 0;
        setRefreshLayoutEnabled();
    }
}