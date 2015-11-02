package com.moobasoft.damego.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.moobasoft.damego.R;
import com.moobasoft.damego.ui.activities.IndexActivity;
import com.moobasoft.damego.ui.fragments.PostsFragment.Mode;
import com.moobasoft.damego.ui.fragments.base.RxFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TagFragment extends RxFragment implements  IndexActivity.ToolbarFragment{

    /** The tag name of the posts to be loaded (includes 'Bookmarks'). */
    private String tagName;
    /** Determine if this is for tags or bookmarks. */
    private Mode mode; // TODO: This should be limited to TAG and BOOKMARKS (make a new enum)

    @Bind(R.id.toolbar) Toolbar toolbar;

    public TagFragment() {}

    public static TagFragment newInstance(Mode mode, String tagName) {
        TagFragment fragment = new TagFragment();
        Bundle args = new Bundle();
        args.putSerializable(PostsFragment.MODE_ARG, mode);
        args.putString(PostsFragment.TAG_NAME_ARG, tagName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        tagName = getArguments().getString(PostsFragment.TAG_NAME_ARG);
        mode = (Mode) getArguments().getSerializable(PostsFragment.MODE_ARG);
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        ButterKnife.bind(this, view);
        onRefresh();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.removeGroup(R.id.fragment_specific_options); //TODO: Why doesn't this work?
        MenuItem unbook = menu.findItem(R.id.action_unbookmark);
        MenuItem book   = menu.findItem(R.id.action_bookmark);
        if (unbook != null && book != null) {
            unbook.setVisible(false);
            book.setVisible(false);
        }
    }

    @Override
    public Toolbar getToolbar() {
        toolbar.setTitle(tagName);
        return toolbar;
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        Fragment content = getChildFragmentManager().findFragmentById(contentView.getId());
        if (content == null) {
            PostsFragment postsFragment = PostsFragment.newInstance(mode, tagName);
            getChildFragmentManager().beginTransaction()
                    .add(contentView.getId(), postsFragment)
                    .commit();
        }
        activateContentView();
    }
}