package com.moobasoft.damego.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.fragments.BookmarksFragment;
import com.moobasoft.damego.ui.fragments.IndexFragment;
import com.moobasoft.damego.ui.fragments.SearchFragment;
import com.moobasoft.damego.ui.fragments.ShowFragment;

public class MainManager {

    enum Tag { INDEX, SHOW, BOOKMARKS, SEARCH }

    private final int containerId;
    private final FragmentManager manager;

    public MainManager(FragmentManager manager, int containerId) {
        this.manager     = manager;
        this.containerId = containerId;
    }

    public void handleBackPress() {
        if (isOnTopOfBackstack(Tag.INDEX)) getIndexFragment().restorePagerPosition();
    }

    public void handleTagClick(String tag) {
        IndexFragment indexFragment = getIndexFragment();
        if (!isOnTopOfBackstack(Tag.INDEX)) {
            loadFragment(indexFragment, Tag.INDEX, true);
            manager.executePendingTransactions();
        }
        indexFragment.setCurrentTag(tag); // Has to be called after state is restored
    }

    public void initialiseFragments() {
        loadFragment(IndexFragment.newInstance(), Tag.INDEX, false);
    }

    // FIXME: Prevent two posts opening at once when jamming the screen
    public void openShowFragment(Post post, String title, boolean openComments) {
        ShowFragment showFragment = ShowFragment.newInstance(post.getId(), title, openComments);
        loadFragment(showFragment, Tag.SHOW, true);
    }

    public void openBookmarksFragment() {
        loadFragment(new BookmarksFragment(), Tag.BOOKMARKS, true);
    }

    public void openSearchFragment() {
        loadFragment(new SearchFragment(), Tag.SEARCH, true);
    }

    public void loadFragment(Fragment fragment, Tag tag, boolean addToBackStack) {
        if (!tag.equals(Tag.INDEX) && isOnTopOfBackstack(Tag.INDEX))
            getIndexFragment().savePagerPosition();

        FragmentTransaction transaction = manager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(containerId, fragment, tag.name());
        if (addToBackStack) transaction.addToBackStack(tag.name());
        transaction.commit();
    }

    private IndexFragment getIndexFragment() {
        return (IndexFragment) manager.findFragmentByTag(Tag.INDEX.name());
    }

    private boolean isOnTopOfBackstack(Tag tag) {
        int count = manager.getBackStackEntryCount();
        return (count == 0 && tag.equals(Tag.INDEX)) || // Empty back stack means Index is visible
               (count > 0 && manager.getBackStackEntryAt(count - 1).getName().equals(tag.name()));
    }
}