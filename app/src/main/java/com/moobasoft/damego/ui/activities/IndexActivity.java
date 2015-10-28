package com.moobasoft.damego.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.moobasoft.damego.R;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.MainManager;
import com.moobasoft.damego.ui.PostsAdapter;
import com.moobasoft.damego.ui.fragments.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IndexActivity extends BaseActivity implements PostsAdapter.PostClickListener, FragmentManager.OnBackStackChangedListener {

    public static final String WAS_TWO_PANE_KEY = "switch_from_two_pane";

    private FragmentManager fragmentManager;
    private MainManager manager;

    @Nullable @Bind(R.id.app_bar)    AppBarLayout appBar;
    @Nullable @Bind(R.id.toolbar)    Toolbar toolbar;
    @Nullable @Bind(R.id.tab_layout) TabLayout tabLayout;
    @Nullable @Bind(R.id.show_container)  ViewGroup showContainer;
    @Bind(R.id.index_container) ViewGroup indexContainer;

    @Nullable public AppBarLayout getAppBar() { return appBar;    }
    @Nullable public Toolbar getToolbar()     { return toolbar;   }
    @Nullable public TabLayout getTabLayout() { return tabLayout; }

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getComponent().inject(this);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);
        manager = new MainManager(showContainer, indexContainer, fragmentManager);

        if (state != null)
            manager.restoreFromInstanceState(state.getBoolean(WAS_TWO_PANE_KEY));
        else
            manager.initialiseFragments();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putBoolean(WAS_TWO_PANE_KEY, manager.isTwoPaneLayout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setMainToolbar();
    }

    public void setMainToolbar() {
        Fragment mainFrag = fragmentManager.findFragmentById(indexContainer.getId());
        if (mainFrag != null) ((BaseFragment)mainFrag).setToolbar();
    }

    @Override
    public void onSummaryClicked(Post post, boolean openComments) {
        manager.openShowFragment(post, getString(R.string.bookmarks_title), openComments);
    }

    @Override
    public void onTagClicked(String tag) {
        manager.handleTagClick(tag);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        manager.handleBackPress();
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

            /** IndexFragment items */
            case R.id.action_bookmarks:
                manager.openBookmarksFragment();
                break;
            case R.id.action_search:
                manager.openSearchFragment();
                break;

            /** IndexActivity items */
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