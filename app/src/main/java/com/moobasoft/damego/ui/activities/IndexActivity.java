package com.moobasoft.damego.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.moobasoft.damego.App;
import com.moobasoft.damego.R;
import com.moobasoft.damego.di.components.DaggerMainComponent;
import com.moobasoft.damego.di.modules.MainModule;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.PostsAdapter;
import com.moobasoft.damego.ui.fragments.IndexFragment;
import com.moobasoft.damego.ui.fragments.ShowFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class IndexActivity extends BaseActivity implements PostsAdapter.PostClickListener {

    public static final String INDEX_TAG = "index";
    public static final String SHOW_TAG  = "show";

    @Nullable @Bind(R.id.toolbar)    Toolbar toolbar;
    @Nullable @Bind(R.id.tab_layout) TabLayout tabLayout;
    @Nullable @Bind(R.id.show_container)  ViewGroup showContainer;
    @Bind(R.id.index_container) ViewGroup indexContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initialiseInjector();

        if (savedInstanceState != null) {
            if (showContainer != null) {
                Fragment showFrag  = getSupportFragmentManager().findFragmentByTag(SHOW_TAG);
                if (showFrag == null)
                    showFrag = ShowFragment.newInstance(0); // Default
                getSupportFragmentManager().popBackStack();
                getSupportFragmentManager().beginTransaction().remove(showFrag).commit();
                getSupportFragmentManager().executePendingTransactions();
                loadFragment(showContainer.getId(), showFrag, SHOW_TAG, false);
            }
        } else {
            loadFragment(indexContainer.getId(), new IndexFragment(), INDEX_TAG, false);
            if (showContainer != null)
                loadFragment(showContainer.getId(), ShowFragment.newInstance(0), SHOW_TAG, true);
        }
    }

    @Nullable public Toolbar getToolbar() {
        return toolbar;
    }

    @Nullable public TabLayout getTabLayout() {
        return tabLayout;
    }

    private void initialiseInjector() {
        DaggerMainComponent.builder()
                .mainModule(new MainModule())
                .appComponent(((App) getApplication()).getAppComponent())
                .build().inject(this);
    }

    @Override
    public void onSummaryClicked(Post post) {
        boolean notTabletLayout = showContainer == null;
        int containerId = (notTabletLayout) ?
                indexContainer.getId() : showContainer.getId() ;
        ShowFragment showFragment = ShowFragment.newInstance(post.getId());
        loadFragment(containerId, showFragment, SHOW_TAG, notTabletLayout);
    }

    private void loadFragment(int containerId, Fragment fragment, String tag, boolean addToBackstack) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, fragment, tag);
        if (addToBackstack) transaction.addToBackStack(tag);
        transaction.commit();
    }

    @Override
    public void onTagClicked(String tag) {
        //viewPager.setCurrentItem(adapter.getIndex(tag), false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        /*boolean loggedIn = credentialStore.isLoggedIn();

        menu.findItem(R.id.action_logout).setVisible(loggedIn);
        menu.findItem(R.id.action_login).setVisible(!loggedIn);
        menu.findItem(R.id.action_register).setVisible(!loggedIn);*/

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
                // credentialStore.delete(); TODO: REACTIVATE
                //Snackbar.make(toolbar, getString(R.string.logout_success), LENGTH_SHORT).show();
                break;
        }
        supportInvalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }

}