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
import android.view.ViewGroup;

import com.moobasoft.damego.R;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.PostsAdapter;
import com.moobasoft.damego.ui.fragments.BaseFragment;
import com.moobasoft.damego.ui.fragments.IndexFragment;
import com.moobasoft.damego.ui.fragments.ShowFragment;
import com.moobasoft.damego.ui.fragments.TagFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IndexActivity extends BaseActivity implements PostsAdapter.PostClickListener, FragmentManager.OnBackStackChangedListener {

    public static final String INDEX_TAG = "index";
    public static final String SHOW_TAG  = "show";
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getComponent().inject(this);
        manager = getSupportFragmentManager();
        manager.addOnBackStackChangedListener(this);

        if (savedInstanceState != null) {
            Fragment showFragment = manager.findFragmentByTag(SHOW_TAG);
            if (showContainer != null)
                restore1024Layout(showFragment);
            else
                restorePortraitLayout(showFragment);
        } else {
            loadFragment(indexContainer.getId(), IndexFragment.newInstance(), INDEX_TAG, false, true);
            if (showContainer != null) {
                ShowFragment showFragment = ShowFragment.newInstance(0, TagFragment.SHOW_ALL_TAG);
                loadFragment(showContainer.getId(), showFragment, SHOW_TAG, false, true);
            }
        }
    }

    private void restorePortraitLayout(Fragment showFragment) {
        if (showFragment != null) {
            manager.beginTransaction().remove(showFragment).commit();
            manager.executePendingTransactions();
            loadFragment(indexContainer.getId(), showFragment, SHOW_TAG, true, true);
        }
    }

    private void restore1024Layout(Fragment showFragment) {
        if (showFragment == null)
            showFragment = ShowFragment.newInstance(0, TagFragment.SHOW_ALL_TAG); // Default
        else {
            if (indexFragmentIsOnTop())
                manager.popBackStackImmediate(SHOW_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            else
                manager.popBackStackImmediate();
            manager.beginTransaction().remove(showFragment).commit();
            manager.executePendingTransactions();
        }
        loadFragment(showContainer.getId(), showFragment, SHOW_TAG, false, true);
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
    public void onSummaryClicked(Post post) {
        int containerId = (isSinglePaneLayout()) ? indexContainer.getId() : showContainer.getId();
        IndexFragment indexFragment = (IndexFragment) manager.findFragmentByTag(INDEX_TAG);
        if (isSinglePaneLayout()) indexFragment.savePagerPosition();
        CharSequence currentTitle = indexFragment.getCurrentTitle();
        ShowFragment showFragment = ShowFragment.newInstance(post.getId(), currentTitle.toString());
        loadFragment(containerId, showFragment, SHOW_TAG, isSinglePaneLayout(), true);
    }

    private void loadFragment(int containerId, Fragment fragment, String tag, boolean addToBackstack, boolean add) {
        FragmentTransaction transaction = manager
                .beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                                     android.R.anim.slide_in_left, R.anim.slide_y);
        if (addToBackstack) transaction.addToBackStack(tag);
        if (add && showContainer == null)
            transaction.add(containerId, fragment, tag);
        else
            transaction.replace(containerId, fragment, tag);
        transaction.commit();
    }

    @Override
    public void onTagClicked(String tag) {
        IndexFragment indexFragment = (IndexFragment) manager.findFragmentByTag(INDEX_TAG);
        ShowFragment showFragment   = (ShowFragment)  manager.findFragmentByTag(SHOW_TAG);

        boolean clickedInShowFragment =
                manager.getBackStackEntryCount() > 0 && (showFragment != null && showFragment.isVisible());

        if (clickedInShowFragment)
            loadFragment(indexContainer.getId(), indexFragment, INDEX_TAG, true, false);

        indexFragment.setCurrentTag(tag);
    }

    /** Disable back button for 2-pane layout */
    @Override
    public void onBackPressed() {
        if (isTwoPaneLayout()) return;
        super.onBackPressed();
        if (isSinglePaneLayout() && indexFragmentIsOnTop()) {
            IndexFragment indexFragment = (IndexFragment) manager.findFragmentByTag(INDEX_TAG);
            indexFragment.restorePagerPosition();
        }
    }

    private boolean indexFragmentIsOnTop() {
        int entryCount = manager.getBackStackEntryCount();
        return entryCount <= 0 || manager.getBackStackEntryAt(entryCount-1).getName().equals(INDEX_TAG);
    }

    /** Restore ViewPager position when naviagating back to IndexFragment */
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
            case R.id.action_search:
                return false;
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