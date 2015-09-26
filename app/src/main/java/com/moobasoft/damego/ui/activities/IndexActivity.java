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
import com.moobasoft.damego.ui.fragments.ShowFragment;
import com.moobasoft.damego.ui.fragments.TagFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class IndexActivity extends BaseActivity implements PostsAdapter.PostClickListener, FragmentManager.OnBackStackChangedListener {

    public static final String HOME_PAGE = "home";

    @Nullable @Bind(R.id.fragment_container)  ViewGroup fragmentContainer;
    @Nullable @Bind(R.id.show_post_container) ViewGroup showPostContainer;
    @Nullable @Bind(R.id.post_index_container) ViewGroup postIndexContainer;

    @Bind(R.id.app_bar_layout) AppBarLayout appBarLayout;
    @Bind(R.id.view_pager)     ViewPager viewPager;
    @Bind(R.id.tab_layout)     TabLayout tabLayout;
    private Adapter adapter;
    private int currentPostId;
    private String currentPage;

    interface MainActions {
        void promptLogin();
        void setToolbar(Toolbar toolbar);
    } // TODO: Use something like this for fragments

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        initialiseInjector();

        adapter = new Adapter(getSupportFragmentManager());

        List<String> tags = Arrays
                .asList("すべて", "Cool", "和製英語", "TOEIC", "馬鹿野郎", "ワンキング", "ゲーム", "CM", "nyans");
        for (String t : tags)
            adapter.addFragment(TagFragment.newInstance(t), t);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        if (savedInstanceState != null) {
            currentPostId = savedInstanceState.getInt(ShowFragment.POST_ID, -1);
            if (showPostContainer != null && currentPostId > 0) {
                getSupportFragmentManager().beginTransaction()
                        .replace(showPostContainer.getId(),
                                ShowFragment.newInstance(currentPostId),
                                ShowFragment.class.getName())
                        .commit();
            }
        } else {
            if (showPostContainer != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(showPostContainer.getId(),
                                ShowFragment.newInstance(10),
                                ShowFragment.class.getName())
                        .commit();
            }
        }
        setToolbar(toolbar, HOME_PAGE);
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
        state.putInt(ShowFragment.POST_ID, currentPostId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        currentPostId = state.getInt(ShowFragment.POST_ID, -1);
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

    @Override
    public void onBackStackChanged() {
        Fragment current = getSupportFragmentManager().findFragmentByTag(ShowFragment.class.getName());
        if (current != null && current.isVisible()) {
        } else {
            setToolbar(toolbar, HOME_PAGE);
        }
    }

    public void setToolbar(Toolbar newToolbar, String tag) {
        setSupportActionBar(newToolbar);

        if (tag.equals(HOME_PAGE)) {
            toolbar.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        } else if (tag.equals(ShowFragment.class.getName())) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
            setTitle(""); // TODO: make this unnecessary
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void onHitBack(String tag) {
        if (ShowFragment.class.getName().equals(tag)) {
            // show home
        }
    }

    @Override
    public void onSummaryClicked(Post post) {
        // TODO: This should be done on rotation!
        // TODO: Maybe manage without backstack (onHitBack())
        //TODO: If (land && ShowFragment on backstack) popBackstack()

        currentPostId = post.getId();
        int containerId = (showPostContainer != null) ?
                showPostContainer.getId() : fragmentContainer.getId();

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId,
                        ShowFragment.newInstance(post.getId()),
                        ShowFragment.class.getName());
        if (showPostContainer == null)
            transaction.addToBackStack(ShowFragment.class.getName());

        transaction.commit();
    }

    @Override
    public void onTagClicked(String tag) {
        viewPager.setCurrentItem(adapter.getIndex(tag), false);
    }

}