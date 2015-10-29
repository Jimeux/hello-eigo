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
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.moobasoft.damego.R;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.activities.BaseActivity;
import com.moobasoft.damego.ui.activities.CreateCommentActivity;
import com.moobasoft.damego.ui.presenters.ShowPresenter;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ShowFragment extends RxFragment implements ShowPresenter.ShowView {

    public static final String POST_KEY    = "post_key";
    public static final String POST_ID_KEY = "post_id";
    public static final String TAG_NAME_KEY = "tag_name";
    public static final String OPEN_COMMENTS_KEY = "open_comments";
    public static final int CONTENT_PAGE = 0;
    public static final int COMMENTS_PAGE = 1;

    private int postIdArg;
    private String tagNameArg;
    private boolean openCommentsArg;
    private Post post;
    private ShowAdapter showAdapter;

    @Inject ShowPresenter presenter;

    @Nullable @Bind(R.id.card_view) CardView cardView;
    @Bind(R.id.tab_layout)          TabLayout tabLayout;
    @Bind(R.id.view_pager)          ViewPager viewPager;
    @Bind(R.id.fab)                 FloatingActionButton fab;

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
        presenter.bindView(this);
        postIdArg = getArguments().getInt(POST_ID_KEY, 0);
        tagNameArg = getArguments().getString(TAG_NAME_KEY);
        openCommentsArg = getArguments().getBoolean(OPEN_COMMENTS_KEY);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //menu.removeGroup(R.id.fragment_specific_options);
        if (post != null) {
            inflater.inflate(R.menu.menu_show, menu);
            menu.findItem(R.id.action_unbookmark).setVisible(post.isBookmarked());
            menu.findItem(R.id.action_bookmark).setVisible(!post.isBookmarked());
        } else
            super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO: Check user is logged in first
        // TODO: Show some kind of loading indication
        if (post == null)
            return super.onOptionsItemSelected(item);
        else if (item.getItemId() == R.id.action_bookmark) {
            presenter.createBookmark(post.getId());
            return false;
        } else if (item.getItemId() == R.id.action_unbookmark) {
            presenter.deleteBookmark(post.getId());
            return false;
        } else return super.onOptionsItemSelected(item);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);
        ButterKnife.bind(this, view);
        tabLayout.setVisibility(GONE);
        if (cardView != null) ViewCompat.setTranslationZ(tabLayout, 2F); // For tablet layout
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
        activateLoadingView();
        presenter.getPost(postIdArg);
    }

    @Override
    public void onPostRetrieved(Post post) {
        this.post = post;
        loadPost(post);
    }

    @Override
    public void onBookmarked(boolean created) {
        post.setBookmarked(created);
        getActivity().supportInvalidateOptionsMenu();
        int resId = created ? R.string.bookmark_created : R.string.bookmark_deleted;
        Snackbar.make(tabLayout, getString(resId), Snackbar.LENGTH_SHORT).show();
    }

    private void loadPost(Post post) {
        getActivity().supportInvalidateOptionsMenu();
        showAdapter = new ShowAdapter(getChildFragmentManager(), post);
        viewPager.setAdapter(showAdapter);
        viewPager.setPageMargin(16);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(CONTENT_PAGE).setIcon(R.drawable.ic_subject_white_24dp);
        tabLayout.getTabAt(COMMENTS_PAGE).setIcon(R.drawable.ic_comment_white_24dp);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(final int position) {
                if (cardView != null) {
                    float elevation = (position == CONTENT_PAGE) ? 2F : 0F;
                    ViewCompat.setElevation(cardView, elevation);
                }
                fab.setVisibility(position == COMMENTS_PAGE ? VISIBLE : GONE);
            }
        });
        if (openCommentsArg) viewPager.setCurrentItem(COMMENTS_PAGE);
        tabLayout.setVisibility(VISIBLE);
        activateContentView();
    }

    @Override
    public void setToolbar() {
        if (toolbar != null) {
            toolbar.setTitle(tagNameArg);
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        setToolbar();
    }

    @Override
    public void onError(int messageId) {
        super.onError(messageId);
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
            if (position == CONTENT_PAGE) {
                ContentFragment fragment = ContentFragment.newInstance(post);
                Animation animation = new TranslateAnimation(0f, 0f, 0f, 1f);
                fragment.setExitTransition(animation);
                return fragment;
            } else
                return CommentsFragment.newInstance(post);
        }
    }
}