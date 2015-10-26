package com.moobasoft.damego.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.fragments.BookmarksFragment;
import com.moobasoft.damego.ui.fragments.IndexFragment;
import com.moobasoft.damego.ui.fragments.ShowFragment;
import com.moobasoft.damego.ui.fragments.TagFragment;

public class MainManager {

    public static final String INDEX_TAG        = "index";
    public static final String SHOW_TAG         = "show";
    public static final String BOOKMARKS_TAG    = "bookmarks";
    public static final String SEARCH_TAG       = "search";

    private final View showContainer;
    private final View indexContainer;
    private final FragmentManager manager;

    public MainManager(View showContainer, View indexContainer, FragmentManager manager) {
        this.showContainer = showContainer;
        this.indexContainer = indexContainer;
        this.manager = manager;
    }

    public boolean isTwoPaneLayout()    { return showContainer != null; }

    public boolean isSinglePaneLayout() { return showContainer == null; }

    public ShowFragment getShowFragment() {
        return (ShowFragment) manager.findFragmentByTag(SHOW_TAG);
    }

    public IndexFragment getIndexFragment() {
        return (IndexFragment) manager.findFragmentByTag(INDEX_TAG);
    }

    public void restoreSingleLayout() {
        ShowFragment showFragment = getShowFragment();
        if (showFragment != null) {
            manager.beginTransaction().remove(showFragment).commit();
            manager.executePendingTransactions();
            loadFragment(indexContainer.getId(), showFragment, SHOW_TAG, true);
        }
    }

    public void restoreTwoPaneLayout() {
        ShowFragment showFragment = getShowFragment();
        if (showFragment == null)
            showFragment = ShowFragment.newInstance(0, TagFragment.SHOW_ALL_TAG, false); // Default
        else {
            if (isOnTopOfBackstack(INDEX_TAG))
                manager.popBackStackImmediate(SHOW_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            else
                manager.popBackStackImmediate();
            manager.beginTransaction().remove(showFragment).commit();
            manager.executePendingTransactions();
        }
        loadFragment(showContainer.getId(), showFragment, SHOW_TAG, false);
    }

    public void loadFragment(int containerId, Fragment fragment, String tag, boolean addToBackstack) {
        FragmentTransaction transaction = manager.beginTransaction()
                        //.setCustomAnimations(R.anim.slide_up_in, R.anim.slide_up_out, 0, 0);
                .replace(containerId, fragment, tag);
        if (addToBackstack) transaction.addToBackStack(tag);
        transaction.commit();
    }

    public boolean isOnTopOfBackstack(String tag) {
        int count = manager.getBackStackEntryCount();
        if (count == 0 && tag.equals(INDEX_TAG))
            return true;
        return count > 0 && manager.getBackStackEntryAt(count-1).getName().equals(tag);
    }

    public void handleBackPress() {
        if (isSinglePaneLayout() && isOnTopOfBackstack(INDEX_TAG)) {
            IndexFragment indexFragment = getIndexFragment();
            indexFragment.restorePagerPosition();
        }

        int backStackEntryCount = manager.getBackStackEntryCount();

        if (isTwoPaneLayout() && backStackEntryCount > 0) {
            String name = manager.getBackStackEntryAt(backStackEntryCount - 1).getName();

            if (name != null && name.equals(SHOW_TAG)) {
                ShowFragment showFragment = getShowFragment();
                manager.popBackStack();
                manager.executePendingTransactions();
                loadFragment(showContainer.getId(), showFragment, SHOW_TAG, false);
                getIndexFragment().restorePagerPosition();
            }
        }
    }

    public void openShowFragment(Post post, String title, boolean openComments) {
        // FIXME: Prevent two posts opening at once when jamming the screen

        int containerId = (isSinglePaneLayout()) ? indexContainer.getId() : showContainer.getId();
        IndexFragment indexFragment = getIndexFragment();

        if (isOnTopOfBackstack(INDEX_TAG)) {
            indexFragment.savePagerPosition();
            title = indexFragment.getCurrentTitle().toString();
        }

        ShowFragment showFragment = ShowFragment.newInstance(post.getId(), title, openComments);
        loadFragment(containerId, showFragment, SHOW_TAG, isSinglePaneLayout());
    }

    public void handleTagClick(String tag) {
        IndexFragment indexFragment = getIndexFragment();
        if (!isOnTopOfBackstack(INDEX_TAG)) {
            loadFragment(indexContainer.getId(), indexFragment, INDEX_TAG, true);
            manager.executePendingTransactions();
        }
        indexFragment.setCurrentTag(tag); // Has to be called after state is restored
    }

    public void restoreFromInstanceState(boolean wasTwoPaneLayout) {
        boolean changedFromTwoPaneLayout = (isSinglePaneLayout() && wasTwoPaneLayout);
        if (isTwoPaneLayout())
            restoreTwoPaneLayout();
        else if (isSinglePaneLayout() && changedFromTwoPaneLayout)
            restoreSingleLayout();
        //else if (isSinglePaneLayout()) // Restore single pane layout
    }

    public void initialiseFragments() {
        loadFragment(indexContainer.getId(), IndexFragment.newInstance(), INDEX_TAG, false);
        if (showContainer != null) {
            ShowFragment showFragment = ShowFragment.newInstance(0, TagFragment.SHOW_ALL_TAG, false);
            loadFragment(showContainer.getId(), showFragment, SHOW_TAG, false);
        }
    }

    public void openBookmarksFragment() {
        loadFragment(indexContainer.getId(), new BookmarksFragment(), BOOKMARKS_TAG, true);
    }
}