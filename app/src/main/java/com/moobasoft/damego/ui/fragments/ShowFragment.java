package com.moobasoft.damego.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.moobasoft.damego.R;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.activities.CreateCommentActivity;
import com.moobasoft.damego.ui.presenters.ShowPresenter;

import org.parceler.Parcels;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ShowFragment extends BaseFragment implements ShowPresenter.ShowView {

    public static final String POST_KEY    = "post_key";
    public static final String POST_ID_KEY = "post_id";
    public static final String TAG_NAME_KEY = "tag_name";
    public static final int CONTENT_PAGE = 0;
    public static final int COMMENTS_PAGE = 1;

    private int postIdArg;
    private String tagNameArg;
    private Post post;
    private ShowAdapter showAdapter;

    @Inject ShowPresenter presenter;

    @Bind(R.id.tab_layout) TabLayout tabLayout;
    @Bind(R.id.view_pager) ViewPager viewPager;
    @Bind(R.id.fab)        FloatingActionButton fab;

    @OnClick(R.id.fab)
    public void clickFab() {
        Intent intent = new Intent(getActivity(), CreateCommentActivity.class);
        intent.putExtra(POST_ID_KEY, post.getId());
        startActivity(intent);
        //doIfLoggedIn(intent); TODO: Forward to activity?
    }

    public ShowFragment() {}

    public static ShowFragment newInstance(int postId, String tagName) {
        ShowFragment fragment = new ShowFragment();
        Bundle args = new Bundle();
        args.putInt(POST_ID_KEY, postId);
        args.putString(TAG_NAME_KEY, tagName);
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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem menuItem = menu.add(0, 723, 1, R.string.action_share)
                .setIcon(R.drawable.ic_share_white_24dp);
        MenuItemCompat.setShowAsAction(menuItem, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);
        ButterKnife.bind(this, view);
        tabLayout.setVisibility(GONE);
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

    private void loadPost(Post post) {
        showAdapter = new ShowAdapter(getChildFragmentManager(), post);
        viewPager.setAdapter(showAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(CONTENT_PAGE).setIcon(R.drawable.ic_subject_white_24dp);
        tabLayout.getTabAt(COMMENTS_PAGE).setIcon(R.drawable.ic_comment_white_24dp);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(final int position) {
                fab.setVisibility(position == COMMENTS_PAGE ? VISIBLE : GONE);
            }
        });
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

    @Override
    public void onError(String message) {
        activateErrorView(message);
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