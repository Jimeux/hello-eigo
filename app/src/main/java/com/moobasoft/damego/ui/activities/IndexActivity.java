package com.moobasoft.damego.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.moobasoft.damego.R;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.PostsAdapter;
import com.moobasoft.damego.ui.fragments.BaseFragment;
import com.moobasoft.damego.ui.fragments.BookmarksFragment;
import com.moobasoft.damego.ui.fragments.IndexFragment;
import com.moobasoft.damego.ui.fragments.ShowFragment;
import com.moobasoft.damego.ui.fragments.TagFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IndexActivity extends BaseActivity implements PostsAdapter.PostClickListener, FragmentManager.OnBackStackChangedListener {

    public static final String CHANGED_FROM_2PANE_KEY = "switch_from_two_pane";
    public static final String INDEX_TAG       = "index";
    public static final String SHOW_TAG        = "show";
    public static final String BOOKMARKS_TAG   = "bookmarks";
    public static final String SEARCH_TAG      = "search";

    private FragmentManager manager;

    @Nullable @Bind(R.id.app_bar)    AppBarLayout appBar;
    @Nullable @Bind(R.id.toolbar)    Toolbar toolbar;
    @Nullable @Bind(R.id.tab_layout) TabLayout tabLayout;
    @Nullable @Bind(R.id.show_container)  ViewGroup showContainer;
    @Bind(R.id.index_container) ViewGroup indexContainer;

    @Nullable public AppBarLayout getAppBar() { return appBar;    }
    @Nullable public Toolbar getToolbar()     { return toolbar;   }
    @Nullable public TabLayout getTabLayout() { return tabLayout; }

    private boolean isTwoPaneLayout()    { return showContainer != null; }
    private boolean isSinglePaneLayout() { return showContainer == null; }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getComponent().inject(this);
        manager = getSupportFragmentManager();
        manager.addOnBackStackChangedListener(this);

        if (state != null) {
            boolean changedFromTwoPaneLayout =
                    (isSinglePaneLayout() && state.getBoolean(CHANGED_FROM_2PANE_KEY));
            Fragment showFragment = manager.findFragmentByTag(SHOW_TAG);

            if (isTwoPaneLayout())
                restoreTwoPaneLayout(showFragment);
            else if (isSinglePaneLayout() && changedFromTwoPaneLayout)
                restoreSingleLayout(showFragment);
            //else if (isSinglePaneLayout()) // Restore single pane layout
        } else {
            loadFragment(indexContainer.getId(), IndexFragment.newInstance(), INDEX_TAG, false);
            if (showContainer != null) {
                ShowFragment showFragment = ShowFragment.newInstance(0, TagFragment.SHOW_ALL_TAG, false);
                loadFragment(showContainer.getId(), showFragment, SHOW_TAG, false);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putBoolean(CHANGED_FROM_2PANE_KEY, isTwoPaneLayout());
    }

    private void restoreSingleLayout(Fragment showFragment) {
        if (showFragment != null) {
            manager.beginTransaction().remove(showFragment).commit();
            manager.executePendingTransactions();
            loadFragment(indexContainer.getId(), showFragment, SHOW_TAG, true);
        }
    }

    private void restoreTwoPaneLayout(Fragment showFragment) {
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

    @Override
    protected void onResume() {
        super.onResume();
        setMainToolbar();
    }

    public void setMainToolbar() {
        Fragment mainFrag = manager.findFragmentById(indexContainer.getId());
        if (mainFrag != null) ((BaseFragment)mainFrag).setToolbar();
    }

    @Override
    public void onSummaryClicked(Post post, boolean openComments) {

        // FIXME: Prevent two posts opening at once when jamming the screen

        int containerId = (isSinglePaneLayout()) ? indexContainer.getId() : showContainer.getId();
        IndexFragment indexFragment = (IndexFragment) manager.findFragmentByTag(INDEX_TAG);

        CharSequence currentTitle = "";
        if (isOnTopOfBackstack(INDEX_TAG)) {
            indexFragment.savePagerPosition();
            currentTitle = indexFragment.getCurrentTitle();
        } else if (isOnTopOfBackstack(BOOKMARKS_TAG))
            currentTitle = getString(R.string.bookmarks_title);

        ShowFragment showFragment = ShowFragment.newInstance(
                post.getId(), currentTitle.toString(), openComments);
        loadFragment(containerId, showFragment, SHOW_TAG, isSinglePaneLayout());
    }

    private void loadFragment(int containerId, Fragment fragment, String tag, boolean addToBackstack) {
        FragmentTransaction transaction = manager
                .beginTransaction()
                //.setCustomAnimations(R.anim.slide_up_in, R.anim.slide_up_out, 0, 0);
                .replace(containerId, fragment, tag);
        if (addToBackstack) transaction.addToBackStack(tag);
        transaction.commit();
    }

    private boolean isOnTopOfBackstack(String tag) {
        int count = manager.getBackStackEntryCount();
        if (count == 0 && tag.equals(INDEX_TAG))
            return true;
        return count > 0 && manager.getBackStackEntryAt(count-1).getName().equals(tag);
    }

    @Override
    public void onTagClicked(String tag) {
        IndexFragment indexFragment = (IndexFragment) manager.findFragmentByTag(INDEX_TAG);

        if (!isOnTopOfBackstack(INDEX_TAG)) {
            loadFragment(indexContainer.getId(), indexFragment, INDEX_TAG, true);
            manager.executePendingTransactions();
        }
        indexFragment.setCurrentTag(tag);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isSinglePaneLayout() && isOnTopOfBackstack(INDEX_TAG)) {
            IndexFragment indexFragment = (IndexFragment) manager.findFragmentByTag(INDEX_TAG);
            indexFragment.restorePagerPosition();
        }

        int backStackEntryCount = manager.getBackStackEntryCount();
        if (isTwoPaneLayout() && backStackEntryCount > 0) {
            String name = manager.getBackStackEntryAt(backStackEntryCount - 1).getName();
            if (name != null && name.equals(SHOW_TAG)) {
                ShowFragment showFragment = (ShowFragment) manager.findFragmentByTag(SHOW_TAG);
                manager.popBackStack();
                manager.executePendingTransactions();
                loadFragment(showContainer.getId(), showFragment, SHOW_TAG, false);
                ((IndexFragment) manager.findFragmentByTag(INDEX_TAG)).restorePagerPosition();
            }
        }
    }

    public void openBookmarks() {
        loadFragment(indexContainer.getId(), new BookmarksFragment(), BOOKMARKS_TAG, true);
    }

    @Override
    public void onBackStackChanged() {
        setMainToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        boolean loggedIn = credentialStore.isLoggedIn();
        menu.findItem(R.id.action_logout).setVisible(loggedIn);
        menu.findItem(R.id.action_login).setVisible(!loggedIn);
        menu.findItem(R.id.action_register).setVisible(!loggedIn);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_login:
                Intent loginIntent = new Intent(this, ConnectActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                loginIntent.putExtra(ConnectActivity.REGISTER, false);
                startActivity(loginIntent);
                break;

            case R.id.action_register:
                Intent registerIntent = new Intent(this, ConnectActivity.class);
                registerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                registerIntent.putExtra(ConnectActivity.REGISTER, true);
                startActivity(registerIntent);
                break;

            case R.id.action_logout:
                credentialStore.delete();
                Snackbar.make(indexContainer, getString(R.string.logout_success), Snackbar.LENGTH_SHORT).show();
                break;
        }
        supportInvalidateOptionsMenu();
        return false;
    }

}

class MainManager {

    public static final String CHANGED_FROM_2PANE_KEY = "switch_from_two_pane";
    public static final String INDEX_TAG       = "index";
    public static final String SHOW_TAG        = "show";
    public static final String BOOKMARKS_TAG   = "bookmarks";
    public static final String SEARCH_TAG      = "search";

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

    public void restoreSingleLayout(Fragment showFragment) {
        if (showFragment != null) {
            manager.beginTransaction().remove(showFragment).commit();
            manager.executePendingTransactions();
            loadFragment(indexContainer.getId(), showFragment, SHOW_TAG, true);
        }
    }

    public void restoreTwoPaneLayout(Fragment showFragment) {
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
        FragmentTransaction transaction = manager
                .beginTransaction()
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

}