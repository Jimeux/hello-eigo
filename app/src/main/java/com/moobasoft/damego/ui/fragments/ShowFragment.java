package com.moobasoft.damego.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moobasoft.damego.R;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.activities.CreateCommentActivity;
import com.moobasoft.damego.ui.activities.IndexActivity;
import com.moobasoft.damego.ui.activities.base.BaseActivity;
import com.moobasoft.damego.ui.fragments.base.RxFragment;
import com.moobasoft.damego.ui.presenters.ShowPresenter;

import org.parceler.Parcels;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ShowFragment extends RxFragment implements ShowPresenter.ShowView, IndexActivity.ToolbarFragment {

    public static final String POST_KEY          = "post_key";
    public static final String POST_ID_KEY       = "post_id";
    public static final String TAG_NAME_KEY      = "tag_name";
    public static final String OPEN_COMMENTS_KEY = "open_comments";
    public static final int CONTENT_PAGE  = 0;
    public static final int COMMENTS_PAGE = 1;
    public static final int BOOKMARK_ID = 444;

    private int postIdArg;
    private String tagNameArg;
    private boolean openCommentsArg;
    private boolean bookmarkRequestOngoing = false;
    private Post post;

    @Inject ShowPresenter presenter;

    /** w1024p view */
    @Nullable @Bind(R.id.title_view) TextView titleView;

    @Bind(R.id.toolbar)    Toolbar toolbar;
    @Bind(R.id.tab_layout) TabLayout tabLayout;
    @Bind(R.id.view_pager) ViewPager viewPager;
    @Bind(R.id.fab)        FloatingActionButton fab;

    @OnClick(R.id.fab)
    public void clickFab() {
        Intent intent = new Intent(getActivity(), CreateCommentActivity.class);
        intent.putExtra(POST_ID_KEY, post.getId());
        ((BaseActivity)getActivity()).doIfLoggedIn(intent);
    }

    public ShowFragment() {}

    public static ShowFragment newInstance(int postId, String tagName, boolean openComments) {
        ShowFragment fragment = new ShowFragment();
        Bundle args = new Bundle();
        args.putInt(POST_ID_KEY, postId);
        args.putString(TAG_NAME_KEY, tagName);
        args.putBoolean(OPEN_COMMENTS_KEY, openComments);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getComponent().inject(this);
        postIdArg       = getArguments().getInt(POST_ID_KEY, 0);
        tagNameArg      = getArguments().getString(TAG_NAME_KEY);
        openCommentsArg = getArguments().getBoolean(OPEN_COMMENTS_KEY);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);
        ButterKnife.bind(this, view);
        tabLayout.setVisibility(GONE);
        presenter.bindView(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null)
            post = Parcels.unwrap(savedInstanceState.getParcelable(POST_KEY));

        if (post != null)
            loadPost(post);
        else
            onRefresh();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.removeGroup(R.id.fragment_specific_options);
        if (post != null) {
            MenuItem item = menu.add( // TODO: ID won't work when same post is open twice
                R.id.fragment_specific_options, postIdArg + BOOKMARK_ID, 0, R.string.action_bookmark);
            MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

            if (bookmarkRequestOngoing)
                item.setIcon(R.drawable.ic_refresh_white_24dp); // TODO: Animate
            else
                item.setIcon(post.isBookmarked() ?
                        R.drawable.ic_bookmark_white_24dp :
                        R.drawable.ic_bookmark_outline_white_24dp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: Check user is logged in first
        if (post == null)
            return super.onOptionsItemSelected(item);
        else if (item.getItemId() == postIdArg + BOOKMARK_ID) {
            if (post.isBookmarked())
                presenter.deleteBookmark(post.getId());
            else
                presenter.createBookmark(post.getId());
            bookmarkRequestOngoing = true;
            getActivity().supportInvalidateOptionsMenu();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putParcelable(POST_KEY, Parcels.wrap(post));
    }

    @Override
    public void onDestroyView() {
        presenter.releaseView();
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        boolean refreshing = (post != null);
        if (refreshing)
            openCommentsArg = (viewPager.getCurrentItem() == COMMENTS_PAGE);

        activateLoadingView();
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) fragments.clear();
        presenter.getPost(refreshing, postIdArg);
    }

    @Override
    public void onPostRetrieved(Post post) {
        this.post = post;
        loadPost(post);
    }

    @Override
    public void onBookmarked(boolean created) {
        bookmarkRequestOngoing = false;
        post.setBookmarked(created);
        getActivity().supportInvalidateOptionsMenu();
        int resId = created ? R.string.bookmark_created : R.string.bookmark_deleted;
        Snackbar.make(tabLayout, getString(resId), Snackbar.LENGTH_SHORT).show();
    }

    private void loadPost(Post post) {
        getActivity().supportInvalidateOptionsMenu();
        ShowAdapter showAdapter = new ShowAdapter(getChildFragmentManager(), post);
        viewPager.setAdapter(showAdapter);
        viewPager.setPageMargin(16);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(final int position) {
                fab.setVisibility(position == CONTENT_PAGE ? GONE : VISIBLE);
            }
        });

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(CONTENT_PAGE).setIcon(R.drawable.ic_subject_white_24dp);
        tabLayout.getTabAt(COMMENTS_PAGE).setIcon(R.drawable.ic_comment_white_24dp);
        tabLayout.setVisibility(VISIBLE);
        if (openCommentsArg) viewPager.setCurrentItem(COMMENTS_PAGE);
        activateContentView();
    }

    @Override
    public Toolbar getToolbar() {
        toolbar.setTitle(tagNameArg);
        if (titleView != null) {
            toolbar.setTitle(""); //TODO: Clean this up
            titleView.setText(tagNameArg);
        }
        return toolbar;
    }

    @Override
    public void onError(int messageId) {
        super.onError(messageId);
        bookmarkRequestOngoing = false;
        getActivity().supportInvalidateOptionsMenu();
    }

    public static class ShowAdapter extends FragmentPagerAdapter {

        private final Post post;

        public ShowAdapter(FragmentManager fm, Post post) {
            super(fm);
            this.post = post;
        }

        @Override
        public int getCount() { return 2; }

        @Override
        public Fragment getItem(int position) {
            if (position == CONTENT_PAGE)
                return ContentFragment.newInstance(post);
            else
                return CommentsFragment.newInstance(post);
        }
    }
}