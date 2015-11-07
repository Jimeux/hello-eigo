package com.moobasoft.helloeigo.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.moobasoft.helloeigo.rest.models.Post;
import com.moobasoft.helloeigo.ui.fragments.TagFragment;
import com.moobasoft.helloeigo.ui.fragments.IndexFragment;
import com.moobasoft.helloeigo.ui.fragments.PostsFragment.Mode;
import com.moobasoft.helloeigo.ui.fragments.SearchFragment;
import com.moobasoft.helloeigo.ui.fragments.ShowFragment;

public class MainManager {

    public enum Tag { INDEX, SHOW, TAG, BOOKMARKS, SEARCH }

    private final int containerId;
    private final FragmentManager manager;

    public MainManager(FragmentManager manager, int containerId) {
        this.manager     = manager;
        this.containerId = containerId;
    }

    public void initialiseFragments() {
        loadFragment(IndexFragment.newInstance(), Tag.INDEX, false);
    }

    // FIXME: Prevent two posts opening at once when jamming the screen
    public void openShowFragment(Post post, String title, boolean openComments) {
        ShowFragment showFragment = ShowFragment.newInstance(post.getId(), title, openComments);
        loadFragment(showFragment, Tag.SHOW, true);
    }

    public void openTagFragment(String tagNameArg) {
        if (manager.getBackStackEntryCount() == 0) {
            IndexFragment indexFragment = (IndexFragment) manager.findFragmentByTag(Tag.INDEX.name());
            if (indexFragment != null) indexFragment.setCurrentTag(tagNameArg);
        } else {
            TagFragment fragment = TagFragment.newInstance(Mode.TAG, tagNameArg);
            loadFragment(fragment, Tag.TAG, true);
        }
    }

    public void openBookmarksFragment(String tagNameArg) {
        TagFragment fragment = TagFragment.newInstance(Mode.BOOKMARKS, tagNameArg);
        loadFragment(fragment, Tag.BOOKMARKS, true);
    }

    public void openSearchFragment() {
        loadFragment(new SearchFragment(), Tag.SEARCH, true);
    }

    public void loadFragment(Fragment fragment, Tag tag, boolean addToBackStack) {
        FragmentTransaction transaction = manager
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .add(containerId, fragment, tag.name());
        if (addToBackStack) transaction.addToBackStack(tag.name());
        transaction.commit();
    }

}