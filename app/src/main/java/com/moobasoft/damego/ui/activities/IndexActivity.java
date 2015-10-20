package com.moobasoft.damego.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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

public class IndexActivity extends BaseActivity implements PostsAdapter.PostClickListener {

    public static final String INDEX_TAG = "index";
    public static final String SHOW_TAG  = "show";

    @Nullable @Bind(R.id.app_bar)    AppBarLayout appBar;
    @Nullable @Bind(R.id.toolbar)    Toolbar toolbar;
    @Nullable @Bind(R.id.tab_layout) TabLayout tabLayout;
    @Nullable @Bind(R.id.show_container)  ViewGroup showContainer;
    @Bind(R.id.index_container) ViewGroup indexContainer;

    @Nullable public AppBarLayout getAppBar() { return appBar; }
    @Nullable public Toolbar getToolbar() { return toolbar; }
    @Nullable public TabLayout getTabLayout() { return tabLayout; }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getComponent().inject(this);
        getSupportFragmentManager().addOnBackStackChangedListener(this::setMainToolbar);

        if (savedInstanceState != null) {
            if (showContainer != null) {
                Fragment showFrag = getSupportFragmentManager().findFragmentByTag(SHOW_TAG);
                if (showFrag == null)
                    showFrag = ShowFragment.newInstance(0, TagFragment.SHOW_ALL_TAG); // Default
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().beginTransaction().remove(showFrag).commit();
                getSupportFragmentManager().executePendingTransactions();
                loadFragment(showContainer.getId(), showFrag, SHOW_TAG, false, true);
            }
        } else {
            loadFragment(indexContainer.getId(), IndexFragment.newInstance(), INDEX_TAG, false, true);
            if (showContainer != null)
                loadFragment(showContainer.getId(),
                        ShowFragment.newInstance(0, TagFragment.SHOW_ALL_TAG), SHOW_TAG, false, true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setMainToolbar();
    }

    public void setMainToolbar() {
        Fragment mainFrag = getSupportFragmentManager()
                .findFragmentById(indexContainer.getId());
        if (mainFrag != null) ((BaseFragment)mainFrag).setToolbar();
    }

    @Override
    public void onSummaryClicked(Post post) {
        boolean notTabletLayout = showContainer == null;
        int containerId = (notTabletLayout) ?
                indexContainer.getId() : showContainer.getId();
        CharSequence currentTitle = ((IndexFragment) getSupportFragmentManager().findFragmentByTag(INDEX_TAG))
                .getCurrentTitle();

        ShowFragment showFragment = ShowFragment.newInstance(post.getId(), currentTitle.toString());
        loadFragment(containerId, showFragment, SHOW_TAG, notTabletLayout, true);
    }

    private void loadFragment(int containerId, Fragment fragment, String tag, boolean addToBackstack, boolean add) {
        FragmentTransaction transaction = getSupportFragmentManager()
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
        IndexFragment indexFrag =
                (IndexFragment) getSupportFragmentManager().findFragmentByTag(INDEX_TAG);

        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();

        indexFrag.setCurrentTag(tag);
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