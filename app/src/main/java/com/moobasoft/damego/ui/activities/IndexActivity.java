package com.moobasoft.damego.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.moobasoft.damego.App;
import com.moobasoft.damego.R;
import com.moobasoft.damego.di.components.DaggerMainComponent;
import com.moobasoft.damego.di.modules.MainModule;
import com.moobasoft.damego.rest.models.Post;
import com.moobasoft.damego.ui.PostsAdapter;
import com.moobasoft.damego.ui.fragments.BaseFragment;
import com.moobasoft.damego.ui.fragments.ShowFragment;
import com.moobasoft.damego.ui.fragments.TagFragment;
import com.moobasoft.damego.ui.presenters.MainPresenter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class IndexActivity extends RxActivity
        implements PostsAdapter.PostClickListener, FragmentManager.OnBackStackChangedListener, MainPresenter.View {

    public static final String HOME_TAG = "home";
    public static final String SHOW_TAG = "show";

    @Nullable @Bind(R.id.fragment_container)   ViewGroup fragmentContainer;
    @Nullable @Bind(R.id.show_post_container)  ViewGroup showPostContainer;
    @Nullable @Bind(R.id.post_index_container) ViewGroup postIndexContainer;

    @Bind(R.id.app_bar_layout) AppBarLayout appBarLayout;
    @Bind(R.id.view_pager)     ViewPager viewPager;
    @Bind(R.id.tab_layout)     TabLayout tabLayout;

    private Adapter adapter;
    private int currentPostId;

    @Inject MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initialiseInjector();
        getSupportFragmentManager().addOnBackStackChangedListener(this);

        presenter.bindView(this);

        if (savedInstanceState != null)
            currentPostId = savedInstanceState.getInt(ShowFragment.POST_ID_KEY, 0);

        tabLayout.setVisibility(View.GONE);
        presenter.getTags();
        activateLoadingView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setToolbar();
    }

    private void loadPost() {
        if (showPostContainer == null) return;

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) // Test for specific tag
            getSupportFragmentManager().popBackStack();

        getSupportFragmentManager().beginTransaction()
                .replace(showPostContainer.getId(),
                        ShowFragment.newInstance(currentPostId),
                        SHOW_TAG)
                .commit();
    }

    private void initialiseInjector() {
        DaggerMainComponent.builder()
                .mainModule(new MainModule())
                .appComponent(((App) getApplication()).getAppComponent())
                .build().inject(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(ShowFragment.POST_ID_KEY, currentPostId);
    }

    @Override
    public void onBackStackChanged() {
        setToolbar();
    }

    //FIXME: Toolbar font is screwed when going back from show > main after config change
    public void setToolbar() {
        FragmentManager manager = getSupportFragmentManager();
        int backStackEntryCount = manager.getBackStackEntryCount();
        Toolbar newToolbar = null;

        if (backStackEntryCount > 0) {
            String current = manager.getBackStackEntryAt(backStackEntryCount - 1).getName();
            newToolbar = ((BaseFragment) manager.findFragmentByTag(current)).getToolbar();
        }

        if (newToolbar == null) {
            appBarLayout.setExpanded(true, false);
            toolbar.setVisibility(View.VISIBLE);
            if (adapter != null) tabLayout.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            setSupportActionBar(toolbar);
        } else {
            setSupportActionBar(newToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onSummaryClicked(Post post) {
        int containerId = (showPostContainer != null) ?
                showPostContainer.getId() : fragmentContainer.getId();
        currentPostId = post.getId();
        ShowFragment showFragment = ShowFragment.newInstance(currentPostId);

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, showFragment, SHOW_TAG);
        if (showPostContainer == null) transaction.addToBackStack(SHOW_TAG);
        transaction.commit();
    }

    @Override
    public void onTagClicked(String tag) {
        viewPager.setCurrentItem(adapter.getIndex(tag), false);
    }

    @Override
    public void onTagsRetrieved(List<String> tags) {
        activateContentView();
        loadPost();
        adapter = new Adapter(getSupportFragmentManager());

        for (String t : tags)
            adapter.addFragment(TagFragment.newInstance(t), t);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        setToolbar();
    }

    @Override
    public void onError(String message) {
        activateErrorView(message);
    }

    @Override
    public void onRefresh() {
        activateLoadingView();
        presenter.getTags();
    }

    static class Adapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        public int getIndex(String tag) {
            return mFragmentTitles.indexOf(tag);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
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
                Snackbar.make(toolbar, getString(R.string.logout_success), LENGTH_SHORT).show();
                break;
        }
        supportInvalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }

}