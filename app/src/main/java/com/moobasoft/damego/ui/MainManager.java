package com.moobasoft.damego.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.fragments.BookmarksFragment;
import com.moobasoft.damego.ui.fragments.IndexFragment;
import com.moobasoft.damego.ui.fragments.SearchFragment;
import com.moobasoft.damego.ui.fragments.ShowFragment;

public class MainManager {

    public static final String INDEX_TAG        = "index";
    public static final String SHOW_TAG         = "show";
    public static final String BOOKMARKS_TAG    = "bookmarks";
    public static final String SEARCH_TAG       = "search";

    private final View indexContainer;
    private final FragmentManager manager;

    public MainManager(View indexContainer, FragmentManager manager) {
        this.indexContainer = indexContainer;
        this.manager = manager;
    }

    public ShowFragment getShowFragment() {
        return (ShowFragment) manager.findFragmentByTag(SHOW_TAG);
    }

    public IndexFragment getIndexFragment() {
        return (IndexFragment) manager.findFragmentByTag(INDEX_TAG);
    }

    public void loadFragment(Fragment fragment, boolean addToBackStack) {
        String tag = null;
        if      (fragment instanceof ShowFragment)      tag = SHOW_TAG;
        else if (fragment instanceof IndexFragment)     tag = INDEX_TAG;
        else if (fragment instanceof SearchFragment)    tag = SEARCH_TAG;
        else if (fragment instanceof BookmarksFragment) tag = BOOKMARKS_TAG;

        int containerId = indexContainer.getId();

        FragmentTransaction transaction = manager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(containerId, fragment, tag);
        if (addToBackStack) transaction.addToBackStack(tag);
        transaction.commit();
    }

    public boolean isOnTopOfBackstack(String tag) {
        int count = manager.getBackStackEntryCount();
        if (count == 0 && tag.equals(INDEX_TAG))
            return true;
        return count > 0 && manager.getBackStackEntryAt(count-1).getName().equals(tag);
    }

    public void handleBackPress() {
        if (isOnTopOfBackstack(INDEX_TAG)) {
            IndexFragment indexFragment = getIndexFragment();
            indexFragment.restorePagerPosition();
        }
    }

    public void openShowFragment(Post post, String title, boolean openComments) {
        // FIXME: Prevent two posts opening at once when jamming the screen

        IndexFragment indexFragment = getIndexFragment();

        if (isOnTopOfBackstack(INDEX_TAG)) {
            indexFragment.savePagerPosition();
            title = indexFragment.getCurrentTitle().toString();
        }

        ShowFragment showFragment = ShowFragment.newInstance(post.getId(), title, openComments);
        loadFragment(showFragment, true);
    }

    public void handleTagClick(String tag) {
        IndexFragment indexFragment = getIndexFragment();
        if (!isOnTopOfBackstack(INDEX_TAG)) {
            loadFragment(indexFragment, true);
            manager.executePendingTransactions();
        }
        indexFragment.setCurrentTag(tag); // Has to be called after state is restored
    }

    public void initialiseFragments() {
        loadFragment(IndexFragment.newInstance(), false);
    }

    public void openBookmarksFragment() {
        loadFragment(new BookmarksFragment(), true);
    }

    public void openSearchFragment() {
        loadFragment(new SearchFragment(), true);
    }
}